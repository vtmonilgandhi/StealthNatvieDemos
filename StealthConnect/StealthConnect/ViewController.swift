//
//  ViewController.swift
//  StealthConnect
//
//  Created by Hardik.Ramoliya on 15/09/22.
//

import UIKit
import Alamofire

class ViewController: UIViewController {
    @IBOutlet weak var txtUsername: UITextField!
    @IBOutlet weak var txtPassword: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
//        txtUsername.layer.borderWidth = 1
//        txtPassword.layer.borderWidth = 1
//        txtUsername.layer.borderColor = UIColor.gray.cgColor
//        txtPassword.layer.borderColor = UIColor.gray.cgColor
//        txtUsername.textColor = .black
//        txtPassword.textColor = .black
        txtUsername.text = "mgtest"
        txtPassword.text = "Password123!"
    }

    
    @IBAction func loginPressed(_ sender: Any) {
        var params: Parameters = [:]
        params["userNameOrEmailAddress"] = txtUsername.text
        params["password"] = txtPassword.text
        
        showLoader()
        let req = Request.login(username: txtUsername.text ?? "", password: txtPassword.text ?? "")
        Task.init {
            await APIManager.shared.getData(of: User.self, req: req) { user, err in
                self.hideLoader()
                if user != nil {
                    UserManager.shared.user = user
                    self.gotoVC(name: String(describing: MenuVC.self))
                } else {
                    self.showAlert(msg: err)
                }
            }
        }        
    }
}

