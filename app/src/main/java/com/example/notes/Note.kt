package com.example.notes

import java.util.*

class Note(nodeId: Int, nodeName: String, nodeDes: String) {
    var nodeID: Int? = nodeId
    var nodeName: String? = nodeName
    var nodeDes: String? = nodeDes
    var createdAt: Calendar = Calendar.getInstance()
}