package com.dimento.app.core

object ValidationConstants {
    const val MAX_EVENT_TEXT_LENGTH = 200
    const val MAX_GROUP_NAME_LENGTH = 100
    const val MAX_GROUP_DESCRIPTION_LENGTH = 200

    const val EVENT_TEXT_BLANK_MESSAGE = "Event text cannot be blank."
    const val EVENT_TEXT_MAX_MESSAGE = "Event text must be at most $MAX_EVENT_TEXT_LENGTH characters."
    const val GROUP_NAME_BLANK_MESSAGE = "Group name cannot be blank."
    const val GROUP_NAME_MAX_MESSAGE = "Group name must be $MAX_GROUP_NAME_LENGTH characters or less."
    const val GROUP_DESCRIPTION_MAX_MESSAGE = "Description must be $MAX_GROUP_DESCRIPTION_LENGTH characters or less."
}
