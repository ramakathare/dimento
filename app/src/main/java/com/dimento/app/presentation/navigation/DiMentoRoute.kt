package com.dimento.app.presentation.navigation

sealed class DiMentoRoute(val route: String) {
    data object Groups : DiMentoRoute("groups")
    data object GroupTimeline : DiMentoRoute("group/{groupId}") {
        fun create(groupId: Long) = "group/$groupId"
    }

    data object CreateEvent : DiMentoRoute("create?groupId={groupId}") {
        fun create(groupId: Long?) = if (groupId == null) "create" else "create?groupId=$groupId"
    }

    data object SelectGroup : DiMentoRoute("selectGroup")
    data object Search : DiMentoRoute("search?groupId={groupId}") {
        fun create(groupId: Long?) = if (groupId == null) "search" else "search?groupId=$groupId"
    }
}
