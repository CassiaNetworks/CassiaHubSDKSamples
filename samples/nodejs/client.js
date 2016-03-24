var thrift = require("thrift");
var ttypes = require("./gen-nodejs/2b_types");
var Controller = require("./gen-nodejs/ControlService");
var Notifier = require('./gen-nodejs/NotificationService');
transport = thrift.TBufferedTransport();
protocol = thrift.TBinaryProtocol();

var ip = "172.16.30.102";

var local = "192.168.10.23";
var port = 9990;
var connection = thrift.createConnection(ip, 9090, {
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


var client = thrift.createClient(Controller, connection);

var address = "F4:06:A5:01:30:1E";
var server = thrift.createServer(Notifier, {
  userChallenge: function(session, result) {
    console.log("user challenge()");
    var info = new AuthInfo();
    info.userId = '123';
    info.name = 'liyi';
    info.secret = "234";
    result(null, info);
  },
  onConnectionStateChange : function(session, chipId, deviceId, status) {
    console.log('connect state change', chipId, deviceId, status);
    if (status == 1) {
      client.discoverServices(deviceId, function(err, response) {
        console.log(err, response);
        //set notification
        client.writeByHandle(address, 19, "0100", function(err, response) {
          console.log("write handle return", err, response);
        });
      });
    }
  },
  onScan : function(session, chipId, device, hexScanRecord, rssi, result) {
    console.log("scan result");
    console.log(chipId, device, hexScanRecord, rssi);
    result(null);
  },
  onServicesDiscovered : function(session, deviceId, s, result) {
    // console.log(deviceId, s);
    var util = require('util');
    console.log(util.inspect(s, {depth:100}));
    result(null);
  },
  onCharacteristicRead: function(session, chipId, deviceId, uuid, hexData) {

  },
  onCharacteristicWrite: function(session, chipId, deviceId, uuid) {

  },
  onCharacteristicChanged: function(session, deviceId, uuid, hexData) {
    console.log(deviceId, uuid, hexData);
  },
  onDescriptorRead: function(session, chipId, deviceId, uuid, hexData) {

  },
  onDescriptorWrite: function(session, chipId, deviceId, uuid) {

  },
  onNotify: function(session, deviceId, handle, hexData) {
    console.log("on notify");
    console.log(deviceId, handle, hexData);
  },
  onReadByHandle: function(session, deviceId, handle, hexData) {
    console.log(deviceId, handle, hexData);
  },
  onMessage : function(session, key, message) {
    console.log(key, message);
  }
});

server.listen(port);

client.setupNotify(local, port, function(err, response) {
  if (err) {
    console.log(err);
    return;
  }
   client.startScan('0', 50000, function(err, response) {
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
