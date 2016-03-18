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
            DatagramSocket clientSocket = new DatagramSocket(); //发送和接收数据报的DatagramSocket对象
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

1. 下载thrift, http://thrift.apache.org/download
2. 编译并安装thrift
  
  ```./configure && make```
 
  注:如果是windows环境,可跳过此步
3. 将文档中的thrift文件保存为Cassia.thrift,生成对应语言的代码,
  
  `thrift --gen java cassia.thrift`
  
  注,window用户,从命令行进入项目目录,输入`thrift-0.9.3.exe --gen java cassia.thrift`

  命令将会把代码生成到当前目录的gen-java目录下
4. 将thrift对应语言的库和生成的代码导入,在thrift-0.9.3/lib 中
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
7. 调用setupNotify告诉CassiaHub本地的ip和端口,让Cassia Hub连接回来
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
  userChallenge: function(session, result) {
    //返回用户认证信息
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
        client.writeByHandle(address, 19, "0100", function(err, response) {
          console.log(err, response);
        });
      });
    }
  },
  onScan : function(session, chipId, device, hexScanRecord, rssi, result) {
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
  onServicesDiscovered : function(session, deviceId, s, result) {
    var util = require('util');
    console.log(util.inspect(s, {depth:100}));
    result(null);
  },
  onNotify: function(session, deviceId, handle, hexData) {
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

Thrift IDL接口定义如下
```thrift
/**
 * Copyright (C) 2016 Cassia Networks Inc.
 * communication with Cassia BluetoothLe Hub
 */
namespace cpp cassiaSDK
namespace py cassiaSDK
namespace java com.cassianetworks.hub.sdk
namespace csharp com.cassianetworks.hub.sdk
//设备id,为蓝牙设备的mac地址
typedef string DeviceId
//芯片id,当前可用的芯片是0和1
typedef string ChipId
/**
 * 用于用户认证,userId和secret请咨询接口人
 */
struct AuthInfo {
    1:string userId
    2:string name
    3:string secret
}
/**
 * 设备信息
 */
struct Device {
  1: DeviceId id //readonly,mac address
  2: string name //readonly
  3: ChipId chipId // 0/1
  4: string type // random/public
}
struct GattService {
    1:string uuid
    2:string desc
    3:i32 startHandle
    4:i32 endHandle
    5:list<Characteristic> chars
}
struct Characteristic {
    1:string uuid
    2:string desc
    3:i32 writeType
    4:i32 property
    5:i32 permission
    6:i32 handle
    7:i32 valueHandle
    8:list<Descriptor> descriptors
}
struct Descriptor {
    1:string uuid
    2:i32 permission
    3:i32 handle
}
service ControlService {
    /**
     * 设置通知服务需要连接的ip和端口,hub将会连接这个端口
     * @param ip IP地址
     * @param port 端口
     */
    void setupNotify(1:string ip, 2:i32 port)
    /**
     * 扫描蓝牙设备,扫描的结果将在onScan中进行通知
     * @param chipId 使用的芯片id
     * @param seconds 扫描时间,0为一直扫描
     */
    void startScan(1:ChipId chipId, 2:i32 seconds)
    /**
     * 停止扫描设备
     * @param chipId 使用的芯片id
     */
    void stopScan(1:ChipId chipId)
    /**
     * 连接蓝牙设备,连接结果在onConnectionStateChange中进行通知
     * @param chipId 使用的芯片id
     * @param deviceId 设备地址
     * @param type 设备地址类型, random/public
     */
    void connect(1:ChipId chipId, 2:DeviceId deviceId, 3:string type)
    /**
     * 断开蓝牙连接,结果在onConnectionStateChange中进行通知
     * @param deviceId 设备地址
     */
    void disconnect(1:DeviceId deviceId)
    /**
     * 获取所有已连接设备
     * @return 设备数组
     */
    list<Device> getConnectedDevices()
    /**
     * 发现设备所有服务,结果在onServicesDiscovered中进行通知
     * @param deviceId 设备地址
     */
    void discoverServices(1:DeviceId deviceId)
    /**
     * 对蓝牙设备的handle进行写入
     * @param deviceId 设备地址
     * @param handle handle号
     * @param hexData 16进制数据
     */
    void writeByHandle(1:DeviceId deviceId, 2:i32 handle, 3:string hexData)
    /**
     * 读取蓝牙设备的handle, 结果在onReadByHandle中进行通知
     * @param deviceId 设备地址
     * @param handle handle号
     */
    void readByHandle(1:DeviceId deviceId, 2:i32 handle)
    /**
     * 通过心跳确保通信
     */
    void heartbeat()
}
service NotificationService {
    /**
     * 用户认证,hub在连接上之后将发送认证请求,返回认证信息
     * @return 用户认证信息
     */
    AuthInfo userChallenge()
    /**
     * 设备连接状态通知
     * @param chipId 使用的芯片id
     * @param deviceId 设备地址
     * @param status 0:断连,1:连接
     */
    void onConnectionStateChange(1:ChipId chipId, 2:DeviceId deviceId, 3:i32 status)
    /**
     * 扫描结果
     * @param chipId 使用的芯片id
     * @param device 设备信息
     * @param hexScanRecord 广播包数据
     * @param rssi 信号强度
     */
    void onScan(1:ChipId chipId, 2:Device device, 3:string hexScanRecord, 4:i32 rssi)
    /**
     * 发现服务结果回调
     * @param deviceId 设备地址
     * @param s 所有服务列表
     */
    void onServicesDiscovered(1:DeviceId deviceId, 2:list<GattService> s)
    /**
     * notification通知数据回调
     * @param deviceId 设备地址
     * @param handle 数据handle号
     * @param hexData 回调的16进制数据
     */
    void onNotify(1:DeviceId deviceId, 2:i32 handle, 3:string hexData)
    /**
     * 读取设备handle通知回调
     * @param deviceId 设备地址
     * @param handle 数据handle号
     * @param hexData 回调的16进制数据
     */
    void onReadByHandle(1:DeviceId deviceId, 2:i32 handle, 3:string hexData)
    /**
     * 消息通知,主要用于其他错误消息通知
     * @param messageKey 消息类型
     * @param params 消息参数
     */
    void onMessage(1:string messageKey, 2:string params)
}
```
