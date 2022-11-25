//
//  Models.swift
//  StealthConnect
//
//  Created by Hardik.Ramoliya on 16/09/22.
//

import Foundation

class User: Decodable {
    var userId: Int?
    var accessToken: String?
}

class Site: Decodable {
    var id: Int?
    var properName: String?
}

class WowzaConnectionResp: Decodable {
    var message: String?
    var success: Bool?
    var isConnected: Bool?
    var isWowzaOnboarded: Bool?
}

class IncidentVideo: Decodable {
    var id: Int
    var original_filename: String
    var resolution: String
    var description: String
}

class Incident: Decodable {
    var id: Int
    var incidentEventType: String
    var incident_datetime: String
    var properName: String
    var siteName: String
    var site_id: Int
    var type_id: Int
}

class Camera: Stream, Decodable {
    var cameraID: Int?
    var cameraName: String?
    var cameraStatus: Bool?
    var highStreamURL: String?
    var lowStreamURL: String?
    var wowzaServerIP: String?
    var monitoringPlatformTypeId: Int?
    
    var monitType: MonitoringPlatform {
        get {
            return MonitoringPlatform(rawValue: monitoringPlatformTypeId ?? 1) ?? .DETEXI_1
        }
    }
    
    func getStreamUrl(isHighStream: Bool) -> String {
        var streamUrl: String?
        switch (monitType) {
        case .Luxriot:
            streamUrl = lowStreamURL
        case .AVIGILON, .GEOVISION_1, .GEOVISION_5:
            if(isHighStream) {
                streamUrl = "rtsp://\(wowzaServerIP ?? ""):1935/connect/\(highStreamURL ?? "").stream"
            } else {
                streamUrl = "rtsp://\(wowzaServerIP ?? ""):1935/connect/\(lowStreamURL ?? "").stream"
            }
        case .DETEXI_1, .DETEXI_3, .DETEXI_6:
            if (isHighStream) {
                streamUrl = "rtsp://\(wowzaServerIP ?? ""):1935/connect/\(highStreamURL ?? "").stream"
            } else {
                streamUrl = "rtsp://\(wowzaServerIP ?? ""):1935/connect/\(lowStreamURL ?? "").stream"
            }
        }
        return streamUrl ?? "";
    }
}
