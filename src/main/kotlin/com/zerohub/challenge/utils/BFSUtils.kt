package com.zerohub.challenge.utils

import org.springframework.stereotype.Component
import java.util.*

@Component
class BFSUtils {


    private class Node (val value:String,val list: MutableList<String>)

    /**
     * finds shortest path from [from] to [to] values
     *
     * [list] contains of a string pairs "from-to"
     *
     * returns list of values to get from [from] to [to] in the shortest way
     * or empty list
     *
     * todo add validations
     * todo move to graphs
     */
    fun findShortestPath (from:String, to:String, list:List<String>) : List<String> {
        val nodes = mutableListOf<Node>()

        //convert list to nodes
        list.forEach {
            val values = it.split("-")
            nodes.find { node->node.value == values[0] }?.list?.add(values[1]) ?:
                    nodes.add(Node( values[0], mutableListOf( values[1])))
        }

        val q = LinkedList(nodes.filter { node->node.value == from }.map { it.value })
        val alreadyVisitedQ = mutableListOf<String>()
        val result = mutableListOf<String>()

        var valueFound = false
        while (q.isNotEmpty()) {
            val currentNode = q.remove()
            if (alreadyVisitedQ.contains(currentNode)) continue

            val currentList = nodes.filter { node->node.value == currentNode }.flatMap { it.list }

            if (currentList.contains(to)) {
                valueFound = true
                result.add(currentNode)
                result.add(to)
                break
            }else {
                result.add(currentNode)
                q.addAll(LinkedList(currentList))
                alreadyVisitedQ.add(currentNode)
            }
        }

        return if (valueFound) result else emptyList()
    }


}