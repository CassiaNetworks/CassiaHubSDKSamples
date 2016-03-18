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
     * @param secure 使用ssl安全连接
     */
    void setupNotify(1:string ip, 2:i32 port, 3:bool secure)

    /**
     * 扫描蓝牙设备,扫描的结果将在onScan中进行通知
     * @param chipId 使用的芯片id
     * @param seconds 扫描毫秒数时间,0为一直扫描
     */
    void startScan(1:ChipId chipId, 2:i32 milliseconds)

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