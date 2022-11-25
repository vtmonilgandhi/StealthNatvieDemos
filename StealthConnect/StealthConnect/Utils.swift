//
//  Utils.swift
//  StealthConnect
//
//  Created by Hardik.Ramoliya on 15/09/22.
//

import UIKit

class UserManager {
    static let shared = UserManager()
    var user: User?
}

extension Array {
    func chunked(into size: Int) -> [[Element]] {
        return stride(from: 0, to: count, by: size).map {
            Array(self[$0 ..< Swift.min($0 + size, count)])
        }
    }
}

enum MonitoringPlatform: Int {
    case GEOVISION_1 = 2
    case GEOVISION_5 = 5
    
    case AVIGILON = 4
    case Luxriot = 7
    
    case DETEXI_1 = 1
    case DETEXI_3 = 3
    case DETEXI_6 = 6
}

extension UIViewController {
    func showAlert(title: String = "Error", msg: String? = "Alert") {
        let ctrl = UIAlertController(title: title, message: msg, preferredStyle: .alert)
        ctrl.show(self, sender: nil)
    }
    
    func gotoVC(name: String) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: String(describing: name))
        navigationController?.pushViewController(vc!, animated: true)
    }
    
    func getVC(name: String) -> UIViewController? {
        return self.storyboard?.instantiateViewController(withIdentifier: String(describing: name))
    }
    
    func showLoader() {
        let indicator = UIActivityIndicatorView(style: .large)
        indicator.startAnimating()
        indicator.layer.name = "indicator"
        indicator.translatesAutoresizingMaskIntoConstraints = false
        self.view.addSubview(indicator)
        self.view.bringSubviewToFront(indicator)
        indicator.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        indicator.centerYAnchor.constraint(equalTo: view.centerYAnchor).isActive = true
    }
    
    func hideLoader() {
        let indicator = self.view.subviews.first(where: { $0.layer.name == "indicator" })
        indicator?.removeFromSuperview()
    }
}

