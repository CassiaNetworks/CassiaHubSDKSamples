# CassiaHubSDKSamples
## 概述
本SDK用于与Cassia Hub进行通讯,连接,控制多个蓝牙设备,
通讯协议基于socket连接,使用了有多种语言库的Thrift协议,
通过建立两个端口(上行/下行)进行双向通信

## 内网发现Cassia Hub
Cassia Hub启动之后,会开始监听UDP广播端口34952, 可通过向内网地址(255.255.255.255) 广播任意"CASSIA_HUB_DISCOVERY"发现Cassia Hub
Cassia Hub收到广播包之后,会用以下格式(ip-port-mac)回复, 用户可以通过ip和端口连接到Cassia Hub,ip和端口即服务地址
注意:SDK运行的程序需要和Hub在同一个内网中才能发现Hub
如果已知Cassia Hub 的ip地址,可以省略此步骤

NodeJs sample:
```javascript
var dgram = require('dgram');
var message = new Buffer("CASSIA_HUB_DISCOVERY");
var client = dgram.createSocket("udp4");
client.bind({'address':'192.168.0.xxx'},//配置一个和Cassia Hub在同一个内网的ip 
function() {
    client.setBroadcast(true);
    client.send(message, 0, message.length, 0x8888, "255.255.255.255");
})
client.on('message', function (message, rinfo) {
    console.log('Message from: ' + rinfo.address + ':' + rinfo.port +' - ' + message);
});
```
PHP sample:
```php
$ip = "255.255.255.255";
$port = 0x8888;
$str = "CASSIA_HUB_DISCOVERY";
$sock = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP); 
socket_set_option($sock, SOL_SOCKET, SO_BROADCAST, 1);
socket_set_option($sock, SOL_SOCKET, SO_RCVTIMEO, array("sec"=>5, "usec"=>0));
//socket_bind($sock, `ifconfig en1 inet | grep inet | awk '{print $2}'`);//指定使用网卡
socket_sendto($sock, $str, strlen($str), 0, $ip, $port);
while(true) {
$ret = @socket_recvfrom($sock, $buf, 50, 0, $ip, $port);
if($ret === false) break;
echo "Messagge : < $buf > , $ip : $port\n";
echo "$buf\n";
echo "$ip\n";
}
socket_close($sock);
```

Java Sample:
```java
import java.io.IOException;
import java.net.*;
public class DiscoverHub {
    public static void main(String[] args) {
        String ip = "255.255.255.255"; //广播地址
        int port = 0x8888;  //udp广播端口
        try {
            DatagramSocket clientSocket = new DatagramSocket(null); //发送和接收数据报的DatagramSocket对象
            clientSocket.bind(new InetSocketAddress("192.168.0.xxx", 0)); //将ip替换为与Hub在同一内网中的地址
            InetAddress address = InetAddress.getByName(ip);
            String sendData = "CASSIA_HUB_DISCOVERY";
            DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes(), sendData.getBytes().length, address, port);
            clientSocket.send(sendPacket); //向hub发送数据报
            byte[] buf = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);
            clientSocket.receive(recvPacket); //接收hub回复的数据报
            System.out.println("receive： " + new String(recvPacket.getData(), 0, recvPacket.getLength()));
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

如果内网中有正在运行的hub,将会收到类似 192.168.0.xx-9090-ae:87:a3:00:e8:00 的字符串,分别为ip,端口,mac地址,ip和端口可用于后面的连接

## 连接Cassia Hub
Cassia Hub使用Thrift进行通讯,Apache Thrift 是 Facebook 实现的一种高效的、支持多种编程语言的远程服务调用的框架。
相关文档请参考http://thrift.apache.org/
中文thrift介绍:http://www.ibm.com/developerworks/cn/java/j-lo-apachethrift/

1. 编译并安装thrift
  
  ```shell
  cd thrift
  ./configure && make
  ```
 
  注:如果是windows环境,可跳过此步直接使用项目中的thrift-0.9.3.exe
2. 通过cassia.thrift文件,生成对应语言的代码
  
  `thrift --gen java cassia.thrift`
  
  注,window用户,从命令行进入项目目录,输入`thrift-0.9.3.exe --gen java cassia.thrift`

  命令将会把代码生成到当前目录的gen-java目录下
4. 将thrift对应语言的库和生成的代码导入,在thrift/lib 中
5. 建立thrift client,使用ControlService,连接到CassiaHub

  ```javascript
  var Controller = require("./gen-nodejs/ControlService");
  transport = thrift.TBufferedTransport();
  protocol = thrift.TBinaryProtocol();
  var ip = "172.16.30.106";
  var connection = thrift.createConnection(ip, 9090, {
    transport : transport,
    protocol : protocol
  });
  var client = thrift.createClient(Controller, connection);
  ```

6. 建立一个thrift server,用来接受从Cassia Hub来的通知
  ```javascript
  var Notifier = require('./gen-nodejs/NotificationService');
  var server = thrift.createServer(Notifier, {
  //当连接上时,Cassia Hub会发送一条userChallenge的通知,请回复您的公司id和secret
    userChallenge: function(session, result) {
      //返回用户认证信息
      console.log("user challenge()");
      var info = new AuthInfo();
      info.userId = '123';
      info.name = 'liyi';
      info.secret = "234";
      result(null, info);
    },
  });
  server.listen(port);
  ```
7. 调用setupNotify告诉CassiaHub本地的ip和端口,让Cassia Hub连接回当前机器,如果连接不成功通知将不能到达
  ```javascript
  client.setupNotify(local, port);
  
  ```
8. 双向通讯通道建立,现在即可以向Cassia Hub发送指令,也可以收到Cassia Hub的通知了

```

+---------------------+                +---------------------+
|  Cassia Hub         |                | User Application    |
|                     |    Control     |                     |
| +-----------------+ |    Service     | +-----------------+ |
| | Thrift Server   |<<<<<<<<<<<<<<<<<<<<| Thrift Client   | |
| | (serve at 9090) | |                | |                 | |
| +-----------------+ |                | +-----------------+ |
|                     |                |                     |
|                     |  Notification  |                     |
| +-----------------+ |    Service     | +-----------------+ |
| | Thrift Client   |>>>>>>>>>>>>>>>>>>>>| ThriftServer    | |
| +-----------------+ |                | +-----------------+ |
+---------------------+                +---------------------+ 

```

接口定义祥见Thrift文件的注释信息

NodeJs Sample:
```javascript
var thrift = require("thrift");
var ttypes = require("./gen-nodejs/2b_types");
var Controller = require("./gen-nodejs/ControlService");
var Notifier = require('./gen-nodejs/NotificationService');
transport = thrift.TBufferedTransport();
protocol = thrift.TBinaryProtocol();
var ip = "172.16.30.106";
var local = "192.168.10.23";
var port = 9997;
//连接CassiaHub
var address = 'xx:xx:xx:xx';
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
var server = thrift.createServer(Notifier, {
  userChallenge: function(result) {
    //返回用户认证信息
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
      client.discoverServices(deviceId, function(err, response) {
        console.log(err, response);
        client.writeByHandle(address, 19, "0100", function(err, response) {
          console.log(err, response);
        });
      });
    }
  },
  onScan : function(chipId, device, hexScanRecord, rssi, result) {
    console.log("scan result");
    console.log(chipId, device, hexScanRecord, rssi);
    if (device.id == address) {
      console.log("found device ", address);
      client.stopScan('0');
      client.connect('0', device.id, device.type, function(err, response) {
        if (err) {
          console.log(err);
        }
      });
    }
    result(null);
  },
  onServicesDiscovered : function(deviceId, s, result) {
    var util = require('util');
    console.log(util.inspect(s, {depth:100}));
    result(null);
  },
  onNotify: function(deviceId, handle, hexData) {
    console.log(deviceId, handle, hexData);
  },
  onReadByHandle: function(deviceId, handle, hexData) {
    console.log(deviceId, handle, hexData);
  },
  onMessage : function(key, message) {
    console.log(key, message);
  }
});

server.listen(port);
//设定ip和端口,让Cassia Hub反向连接过来,发送通知
client.setupNotify(local, port, function(err, response) {
  if (err) {
    console.log(err);
    return;
  }
  client.connect('0', address, "random", function(err, response) {
    if (err) {
      console.log(err);
      return;
    }
  });
});
```
