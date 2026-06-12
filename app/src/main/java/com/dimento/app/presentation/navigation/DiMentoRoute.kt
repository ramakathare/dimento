package com.dimento.app.presentation.navigation

sealed class DiMentoRoute(val route: String) {
    data object Groups : DiMentoRoute("groups")
    data object GroupTimeline : DiMentoRoute("group/{groupId}?editEventId={editEventId}") {
        fun create(groupId: Long, editEventId: Long = -1L) =
            if (editEventId > 0) "group/$groupId?editEventId=$editEventId"
            else "group/$groupId"
    }

    data object SelectGroup : DiMentoRoute("selectGroup")
    data object Search : DiMentoRoute("search?groupId={groupId}") {
        fun create(groupId: Long?) = if (groupId == null) "search" else "search?groupId=$groupId"
    }
}
