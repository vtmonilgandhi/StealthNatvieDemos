//
//  Stream.swift
//  StealthConnect
//
//  Created by Hardik.Ramoliya on 19/09/22.
//

import Foundation
import MobileVLCKit

class Stream {
    var isConnected: Bool = false
    var isWowzaOnboarded: Bool = false
    var ConnectionString: String = ""
    //options: ["--no-drop-late-frames", "--no-skip-frames", "-vvv", "--rtsp-tcp"]
    var mediaPlayer = VLCMediaPlayer()
    var ConnectionRetryCount = 0
    var ConnectionRetryTimes = 2
    
    var RetryCount = 0
    var RetryTimes = 5
    
    func connectStream(type: MonitoringPlatform, _ completion: @escaping (Bool) -> Void) async {
        switch (type) {
        case .Luxriot:
            isConnected = true
        default:
            await connectToWowza({ val in
                completion(val)
            })
        }
    }
    
    func disConnectStream(type: MonitoringPlatform) async {
        switch (type) {
        case .Luxriot:
            isConnected = true;
        default:
            disconnectFromWowza()
        }
    }
    
    func playStream() {
        mediaPlayer.play()
    }
    
    func stopStream() {
        mediaPlayer.stop()
    }
    
    func connectToWowza( _ completion: @escaping (Bool) -> Void) async {
        NSLog("ConnectToWowza -> \(ConnectionString)")
        ConnectionRetryCount = 0
        isConnected = false
        while (!isConnected && (ConnectionRetryCount <= ConnectionRetryTimes)) {
            ConnectionRetryCount = ConnectionRetryCount + 1
            await APIManager.shared.getData(of: WowzaConnectionResp.self, req: .connectCamera(name: ConnectionString)) { [self] connectResp, err in
                self.isConnected = (connectResp == nil ? false : connectResp?.isConnected) ?? false
                self.isWowzaOnboarded = (connectResp == nil ? false : connectResp?.isWowzaOnboarded) ?? false
                NSLog("ConnectToWowza -> \(ConnectionString) IsConnected -> \(isConnected) IsWowzaOnboarded -> \(isWowzaOnboarded)")
                completion(isConnected)
            }
        }
    }
    
    func disconnectFromWowza() {
        NSLog("DisconnectCamera -> \(ConnectionString)")
        Task.init {
            await APIManager.shared.getData(of: WowzaConnectionResp.self, req: .disconnectCamera(name: ConnectionString)) { [self] connectResp, err in
                self.isConnected = (connectResp == nil ? false : connectResp?.isConnected) ?? false
                self.isWowzaOnboarded = (connectResp == nil ? false : connectResp?.isWowzaOnboarded) ?? false
            }
        }
    }
}
