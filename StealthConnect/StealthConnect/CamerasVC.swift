//
//  CamerasVC.swift
//  StealthConnect
//
//  Created by Hardik.Ramoliya on 16/09/22.
//

import UIKit
import MobileVLCKit

class CameraCell: UICollectionViewCell, VLCMediaPlayerDelegate {
    @IBOutlet weak var lblName: UILabel!
    @IBOutlet weak var lblConnection: UILabel!
    @IBOutlet weak var videoView: UIView!
    
    var isLoading = false
    let str = Stream()
    var camera: Camera?
    
    func loadStream() async {
        str.ConnectionString = (false ? (camera?.highStreamURL)! : (camera?.lowStreamURL)!)
//        print("\(Date()) loadStream -> \(str.ConnectionString)")
        
        await str.connectStream(type: (camera?.monitType)!, { [self] isConnected in
            isLoading = isConnected ? false : true
            lblConnection.isHidden = isConnected ? true : false
            lblConnection.text = str.isConnected ? "" : "OFFLINE"
            
            if isConnected {
                PlayStream()
            }
        })
    }
    
    func PlayStream()
    {
        guard let strURL = camera?.getStreamUrl(isHighStream: false),
              let url = URL(string: strURL) else {
            return
        }
//        print("\(Date()) PlayStream -> \(str.ConnectionString)")
        str.mediaPlayer.delegate = self
        str.mediaPlayer.drawable = videoView
        str.mediaPlayer.media?.addOptions(["hardware-decoding":false])
        str.mediaPlayer.media = VLCMedia(url: url)
        str.playStream()
    }
        func mediaPlayerStateChanged(_ aNotification: Notification) {
            guard let videoPlayer = aNotification.object as? VLCMediaPlayer else {return}
            switch videoPlayer.state {
            case .playing:
                NSLog("\(str.ConnectionString) PlayerState -> playing")
            case .error:
                NSLog("\(str.ConnectionString) PlayerState -> error")
                Task.init {
                    await loadStream()
                }
            default:
                break
            }
        }
}

class CamerasVC: UIViewController {
    @IBOutlet weak var colView: UICollectionView!
    @IBOutlet weak var segmentedControl: UISegmentedControl!
    @IBOutlet weak var lblPageStatus: UILabel!
    @IBOutlet weak var btnPrev: UIButton!
    @IBOutlet weak var btnNext: UIButton!
    
    var cameras: [Camera] = []
    var siteID: Int = 0
    var cameraStreams: [Stream] = []
    var isHighDefination = true
    var pageSize = 0
    var currentPage = 1
    
    override func viewDidLoad() {
        super.viewDidLoad()
        cameraStreams.removeAll()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(true)
        StopActiveStreams()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        Task.init {
            await getCameraList()
        }
    }
    
    @IBAction func btnPrevPressed(_ sender: UIButton) {
        if(currentPage > 0) {
            StopActiveStreams()
            currentPage = currentPage == 1 ? 1 : (currentPage - 1)
            lblPageStatus.text = "\(currentPage) / \(pageSize)"
            self.colView.reloadData()
        }
    }
    
    @IBAction func btnNextPressed(_ sender: UIButton) {
        let curr = currentPage + 1
        if(curr <= pageSize) {
            StopActiveStreams()
            currentPage = (curr > pageSize) ? pageSize : (currentPage + 1)
            lblPageStatus.text = "\(currentPage) / \(pageSize)"
            self.colView.reloadData()
        }
    }
    
    @IBAction func indexChanged(_ sender: UISegmentedControl) {
        switch segmentedControl.selectedSegmentIndex {
        case 0:
            StopActiveStreams()
            isHighDefination = true
            self.colView.reloadData()
        case 1:
            StopActiveStreams()
            isHighDefination = false
            self.colView.reloadData()
        default:
            break
        }
    }
    
    func StopActiveStreams() {
        for stream in cameraStreams {
            stream.stopStream()
            stream.disconnectFromWowza()
        }
        cameraStreams.removeAll()
    }
    
    func getCameraList() async {
        showLoader()
        await APIManager.shared.getData(of: [Camera].self, req: .getCameras(siteID: siteID), isV2: true) { cameras, err in
            self.hideLoader()
            
            self.pageSize = cameras?.chunked(into: 6).count ?? 0
            self.lblPageStatus.text = "\(self.currentPage) / \(self.pageSize)"
            
            if let cameraArray = cameras {
                let camDict: [Int: String] = cameraArray.reduce(into: [:], { result, next in
                    result[next.cameraID!] = next.cameraName!
                })
                
                let sortedCam = camDict.sorted {
                    (s1, s2) -> Bool in return s1.value.localizedStandardCompare(s2.value) == .orderedAscending
                }
                
                sortedCam.forEach { cam in
                    let sortedCameras = cameraArray.first { camera in
                        ((camera.cameraID == cam.key) && (camera.cameraName == cam.value))
                    }
                    self.cameras.append(sortedCameras!)
                }
            }
            self.colView.reloadData()
        }
    }
}

extension CamerasVC: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if(pageSize == 0) {
            return 0
        } else {
            if(currentPage < pageSize) {
                return ((currentPage * 6) - ((currentPage - 1) * 6))
            } else {
                return cameras.count - ((currentPage - 1) * 6)
            }
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: (collectionView.frame.width/2) - 15, height: (collectionView.frame.width/2) - 15)
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "CameraCell", for: indexPath) as? CameraCell else {
            return UICollectionViewCell()
        }
        let index = ((currentPage - 1) * 6) + indexPath.row
        cell.lblName.text = cameras[index].cameraName
        cell.contentView.layer.borderWidth = 1
        cell.contentView.layer.borderColor = UIColor.gray.cgColor
        cell.camera = cameras[index]
        cell.isLoading = true
        cell.lblConnection.isHidden = false
        cell.lblConnection.text = "CONNECTING..."
        cameraStreams.append(cell.str)
        Task.init {
            await cell.loadStream()
        }
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        guard let vc = getVC(name: String(describing: CameraDetailsVC.self)) as? CameraDetailsVC else {
            return
        }
        vc.camera = cameras[indexPath.row]
        if((vc.camera?.isConnected) != nil) {
            navigationController?.pushViewController(vc, animated: true)
        }
    }
}
