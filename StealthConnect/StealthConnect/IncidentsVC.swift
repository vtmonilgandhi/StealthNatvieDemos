//
//  IncidentsVC.swift
//  StealthConnect
//
//  Created by Hardik.Ramoliya on 16/09/22.
//

import UIKit

class IncidentCell: UICollectionViewCell {
    @IBOutlet weak var lblSiteName: UILabel!
    @IBOutlet weak var lblIncidentId: UILabel!
    @IBOutlet weak var lblIncidentType: UILabel!
    @IBOutlet weak var lblIncidentDate: UILabel!
}


class IncidentsVC: UIViewController {
    
    @IBOutlet weak var colView: UICollectionView!
    var incidentList: [Incident] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        Task.init {
            await getIncidents()
        }
    }
    
    
    func getIncidents() async {
        showLoader()
        await APIManager.shared.getData(of: [Incident].self, req: .GetIncidents(maxResult: 0), isV2: true) { incidents, err in
            self.hideLoader()
            
            if let incidentArray = incidents {
                self.incidentList = incidentArray
            }
            self.colView.reloadData()
        }
    }
}

extension IncidentsVC: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return incidentList.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "IncidentCell", for: indexPath) as? IncidentCell else {
            return UICollectionViewCell()
        }
        
        cell.lblIncidentId.text = "\(incidentList[indexPath.row].id)"
        cell.lblIncidentType.text = incidentList[indexPath.row].incidentEventType
        cell.lblSiteName.text = incidentList[indexPath.row].siteName
        
        let dateFormatterGet = DateFormatter()
        dateFormatterGet.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

        let dateFormatterPrint = DateFormatter()
        dateFormatterPrint.dateFormat = "MMM dd, yyyy"

        if let date = dateFormatterGet.date(from: incidentList[indexPath.row].incident_datetime) {
            cell.lblIncidentDate.text = dateFormatterPrint.string(from: date)
        } else {
           print("There was an error decoding the string")
        }
        
        cell.contentView.layer.borderWidth = 1
        cell.contentView.layer.borderColor = UIColor.gray.cgColor
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        guard let vc = getVC(name: String(describing: IncidentVideoVC.self)) as? IncidentVideoVC else {
            return
        }
        showLoader()
        Task.init {
            await APIManager.shared.getData(of: [IncidentVideo].self, req: .GetIncidentVideo(incidentId: incidentList[indexPath.row].id), isV2: true) { url, err in
                self.hideLoader()
                
                vc.strURL = url?[0].original_filename
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }
}
