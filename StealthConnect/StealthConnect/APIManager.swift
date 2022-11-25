//
//  APIManager.swift
//  StealthConnect
//
//  Created by Hardik.Ramoliya on 15/09/22.
//

import Foundation
import Alamofire

enum Request {
    case login(username: String, password: String)
    case getSites
    case getCameras(siteID: Int)
    case connectCamera(name: String)
    case disconnectCamera(name: String)
    case GetIncidents(maxResult: Int)
    case GetIncidentVideo(incidentId: Int)
}

class ErrorResponse: Decodable {
    var code: String?
    var message: String?
    var details: String?
}

class Response<T: Decodable>: Decodable {
    var result: T?
    var success: Bool?
    var error: ErrorResponse?
}

class APIManager {
    static let shared = APIManager()
    let baseURL = "https://portal-gw-dev.stealthmonitoring.net"
    
    func getData<T: Decodable>(of: T.Type, req: Request, isV2: Bool = false, completion: @escaping ((T?, String?) -> Void)) async {
        var reqURL = baseURL
        var reqMethod = HTTPMethod.get
        var reqParam: Parameters? = nil
        var reqHeaders: HTTPHeaders = []
        
        if UserManager.shared.user?.accessToken == nil {
            reqHeaders = [.contentType("application/json")]
        } else {
            reqHeaders = [.contentType("application/json"),
                          .authorization(bearerToken: "\(UserManager.shared.user?.accessToken ?? "")")]
        }
        getRequest(req: req, reqURL: &reqURL, reqMethod: &reqMethod, reqParam: &reqParam)
        
        let request = AF.request(reqURL, method: reqMethod, parameters: reqParam, encoding: JSONEncoding.default, headers: reqHeaders)
        //        request.responseJSON() { data in
        //            print(data)
        //        }
        if isV2 {
            request.responseDecodable(of: T.self) { (data) in
                switch data.result {
                case .success(let response):
                    completion(response, nil)
                case .failure(let error):
                    completion(nil, error.localizedDescription)
                }
            }
        } else {
            request.responseDecodable(of: Response<T>.self) { (data) in
                switch data.result {
                case .success(let response):
                    if response.result != nil {
                        completion(response.result, nil)
                    } else {
                        completion(nil, response.error?.details ?? response.error?.message)
                    }
                case .failure(let error):
                    completion(nil, error.localizedDescription)
                }
            }
        }
    }
    
    private func getRequest(req: Request, reqURL: inout String, reqMethod: inout HTTPMethod, reqParam: inout Parameters?) {
        switch req {
        case .login(let username, let password):
            let params: Parameters = ["userNameOrEmailAddress": username,
                                      "password": password]
            reqURL = reqURL + "/api/TokenAuth/Authenticate"
            reqMethod = .post
            reqParam = params
        case .getSites:
            reqURL = reqURL + "/api/services/app/Site/GetAllSitesByUser"
            reqMethod = .get
            reqParam = nil
        case .getCameras(let siteID):
            reqURL = reqURL + "/api/Sites/\(siteID)/cameras"
            reqMethod = .get
            reqParam = nil
        case .connectCamera(let name):
            reqURL = reqURL + "/api/services/app/Wowza/ConnectWowzaStreamByName?streamFileName=\(name)"
            reqMethod = .post
            reqParam = nil
        case .disconnectCamera(let name):
            reqURL = reqURL + "/api/services/app/Wowza/DisconnectWowzaStreamByName?streamFileName=\(name)"
            reqMethod = .post
            reqParam = nil
        case .GetIncidents(let maxResult):
            reqURL = reqURL + "/api/incidents?maxResultCount=\(maxResult)"
            reqMethod = .get
            reqParam = nil
        case .GetIncidentVideo(let incidentId):
            reqURL = reqURL + "/api/incidents/\(incidentId)/videos"
            reqMethod = .get
            reqParam = nil
        }
    }
}
