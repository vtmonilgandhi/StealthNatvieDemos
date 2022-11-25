//
//  SitesVC.swift
//  StealthConnect
//
//  Created by Hardik.Ramoliya on 16/09/22.
//

import UIKit

class SitesVC: UIViewController {

    @IBOutlet weak var tblView: UITableView!
    var sites: [Site] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        Task.init {
            await getSiteList()
        }
    }
    
    func getSiteList() async {
        showLoader()
        await APIManager.shared.getData(of: [Site].self, req: .getSites) { sites, err in
            self.hideLoader()
            self.sites = sites ?? []
            self.tblView.reloadData()
        }
    }
}

extension SitesVC: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return sites.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "SiteCell") else {
            return UITableViewCell()
        }
        cell.textLabel?.text = sites[indexPath.row].properName
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let vc = getVC(name: String(describing: CamerasVC.self)) as? CamerasVC else {
            return
        }
        vc.siteID = sites[indexPath.row].id ?? 0
        navigationController?.pushViewController(vc, animated: true)
    }
}
