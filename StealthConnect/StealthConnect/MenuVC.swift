//
//  MenuVC.swift
//  StealthConnect
//
//  Created by Hardik.Ramoliya on 15/09/22.
//

import UIKit

class MenuVC: UIViewController {
    let menus = ["Sites", "Incidents"]

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
}

extension MenuVC: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return menus.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MenuCell")
        cell?.textLabel?.text = menus[indexPath.row]
        return cell ?? UITableViewCell()
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.row == 0 {
            gotoVC(name: String(describing: SitesVC.self))
        } else if indexPath.row == 1 {
            gotoVC(name: String(describing: IncidentsVC.self))
        }
    }
}
