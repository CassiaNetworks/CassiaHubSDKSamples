'use strict'

var dgram = require('dgram');

var thrift = require("thrift");
var Controller = require("./gen-nodejs/ControlService");
var Notifier = require('./gen-nodejs/NotificationService');

var message = new Buffer("CASSIA_HUB_DISCOVERY");
var client = dgram.createSocket("udp4");

var hubIP,
    hubPort,
    localIP,
    localPort = 9999;
var client;

//发现hub
client.bind(
  {},//配置一个和Cassia Hub在同一个内网的ip 
  function() {
      client.setBroadcast(true);
      client.send(message, 0, message.length, 0x8888, "255.255.255.255");

  }
)
client.on('message', function (message, rinfo) {
    console.log('Message from: ' + rinfo.address + ':' + rinfo.port +' - ' + message);
    message = message.toString();
    var msgArr = message.split('-');
    hubIP = msgArr[0];
    hubPort = msgArr[1];

    var connection = thrift.createConnection(hubIP, hubPort, {
      transport : transport,
      protocol : protocol
    });

    connection.on('close', function() {
      console.log("connection close");
      process.exit();
    });
    connection.on('connect', function() {
      console.log("connection establish");
    });
    connection.on('error', function(err) {
      console.log(err);
    });

    client = thrift.createClient(Controller, connection);
    client.setupNotify(localIP, localPort, false, function(err) {
      if (err) {
        console.log(err);
        return;
      }

      client.startScan('0', 10000, function(err) {
        if (err) {
          console.log(err);
        }
      });

      // client.connect('0', address, "public", function(err, response) {
      //   if (err) {
      //     console.log(err);
      //     return;
      //   }
      // });



    });

});


var transport = thrift.TBufferedTransport();
var protocol = thrift.TBinaryProtocol();


//获取本机ip
var os = require('os');
let netInterface = os.networkInterfaces();
for(let k in netInterface) {
  if(k == 'vboxnet0') continue;
  netInterface[k].forEach(function (v) {
    if(v.internal == false && v.family == 'IPv4') {
      localIP = v.address;
    }
  })
}
console.log(localIP)

var server = thrift.createServer(Notifier, {
  userChallenge: function(result) {
    console.log("user challenge()");
    var info = new AuthInfo();
    info.userId = '123';
    info.name = 'liyi';
    info.secret = "234";
    result(null, info);
  },
  onConnectionStateChange : function(chipId, deviceId, status) {
    console.log('connect state change', chipId, deviceId, status);
    if (status == 1) {
      client.discoverServices(deviceId, function(err) {
        //set notification
        // client.writeByHandle(address, 19, "0100", function(err, response) {
        //   console.log("write handle return", err, response);
        // });
      });
      client.getConnectedDevices(function(err, lists) {
        if(err) console.error(err);
        console.log('connectedDevices: ')
        console.log(lists);
      })
    }
  },
  onScan : function(chipId, device, hexScanRecord, rssi, result) {
    // if(device.id == address) {
      console.log("scan result");
      console.log(chipId, device, hexScanRecord, rssi);
      client.connect('1', device.id, 'public')
    // }
    result(null);
  },
  onServicesDiscovered : function(deviceId, s, result) {
    console.log('service', deviceId)
    var util = require('util');
    console.log(util.inspect(s, {depth:100}));
    result(null);
  },
  onCharacteristicRead: function(chipId, deviceId, uuid, hexData) {

  },
  onCharacteristicWrite: function(chipId, deviceId, uuid) {

  },
  onCharacteristicChanged: function(deviceId, uuid, hexData) {
    console.log(deviceId, uuid, hexData);
  },
  onDescriptorRead: function(chipId, deviceId, uuid, hexData) {

  },
  onDescriptorWrite: function(chipId, deviceId, uuid) {

  },
  onNotify: function(deviceId, handle, hexData) {
    console.log("on notify");
    console.log(deviceId, handle, hexData);
  },
  onReadByHandle: function(deviceId, handle, hexData) {
    console.log(deviceId, handle, hexData);
  },
  onMessage : function(key, message) {
    console.log(key, message);
  }
});

server.listen(localPort);
