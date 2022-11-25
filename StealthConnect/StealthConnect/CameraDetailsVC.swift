//
//  CameraDetailsVC.swift
//  StealthConnect
//
//  Created by Hardik.Ramoliya on 16/09/22.
//

import UIKit
import MobileVLCKit

class CameraDetailsVC: UIViewController {
    
    @IBOutlet weak var videoView: UIView!
    @IBOutlet weak var segmentedControl: UISegmentedControl!
    @IBOutlet weak var lblConnection: UILabel!
    
    var isLoading = false
    var mediaPlayer = VLCMediaPlayer()
    var camera: Camera?
    var isHighDefination = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        isLoading = true
        lblConnection.isHidden = false
        Task.init {
            await         playStream()
        }
    }
    
    @IBAction func indexChanged(_ sender: UISegmentedControl) {
        switch segmentedControl.selectedSegmentIndex {
        case 0:
            isHighDefination = true
            Task.init {
                await         playStream()
            }
        case 1:
            isHighDefination = false
            Task.init {
                await         playStream()
            }
        default:
            break
        }
    }
    
    func playStream() async {
        
        guard let strURL = camera?.getStreamUrl(isHighStream: false),
              let url = URL(string: strURL) else {
            return
        }
        
        let str = Stream()
        await str.connectStream(type: (camera?.monitType)!, { isConnected in
            self.isLoading = isConnected ? false : true
            self.lblConnection.isHidden = isConnected ? true : false
            self.lblConnection.text = str.isConnected ? "" : "OFFLINE"
        })
        
        mediaPlayer.drawable = videoView
        mediaPlayer.media?.addOptions(["hardware-decoding":false])
        mediaPlayer.media = VLCMedia(url: url)
        mediaPlayer.play()
    }
    
}
