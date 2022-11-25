//
//  IncidentVideoViewController.swift
//  StealthConnect
//
//  Created by Monil Gandhi on 20/10/22.
//

import UIKit
import MobileVLCKit

class IncidentVideoVC: UIViewController {
    
    @IBOutlet weak var lblStartTime: UILabel!
    @IBOutlet weak var lblTotalTime: UILabel!
    @IBOutlet weak var videoView: UIView!
    @IBOutlet weak var seekBar: UIProgressView!
    
    var isLoading = false
    var mediaPlayer = VLCMediaPlayer()
    var strURL: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        isLoading = true
        playStream()
    }
    
    @IBAction func handlePlayPause(_ sender: UIButton) {
        if mediaPlayer.isPlaying {
            mediaPlayer.pause()
            sender.isSelected = true
        } else {
            mediaPlayer.play()
            sender.isSelected = false
        }
    }
    
    func playStream() {
        if let url = strURL {
            mediaPlayer.drawable = videoView
            mediaPlayer.delegate = self
            mediaPlayer.media?.addOptions(["hardware-decoding":false])
            mediaPlayer.media = VLCMedia(url: URL(string: url)!)
            mediaPlayer.play()
        }
    }
}

extension IncidentVideoVC: VLCMediaPlayerDelegate {
    
    func mediaPlayerTimeChanged(_ aNotification: Notification) {
        
        lblStartTime.text = "\(mediaPlayer.time)"
        lblTotalTime.text = mediaPlayer.media?.length.stringValue
        
        seekBar.progress = Float(mediaPlayer.position)
    }
}
