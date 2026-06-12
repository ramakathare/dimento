package com.dimento.app.core

import android.content.Context
import androidx.room.Room
import com.dimento.app.data.local.DiMentoDatabase
import com.dimento.app.data.repository.MemoryRepositoryImpl
import com.dimento.app.data.search.KeywordTokenizer
import com.dimento.app.domain.repository.MemoryRepository
import com.dimento.app.domain.usecase.CreateEventUseCase
import com.dimento.app.domain.usecase.CreateGroupUseCase
import com.dimento.app.domain.usecase.DeleteEventUseCase
import com.dimento.app.domain.usecase.DeleteGroupUseCase
import com.dimento.app.domain.usecase.EnsureDefaultGroupUseCase
import com.dimento.app.domain.usecase.ExportEventsCsvUseCase
import com.dimento.app.domain.usecase.ExportGroupEventsCsvUseCase
import com.dimento.app.domain.usecase.ForwardEventUseCase
import com.dimento.app.domain.usecase.GetEventsDueTodayUseCase
import com.dimento.app.domain.usecase.GetGroupUseCase
import com.dimento.app.domain.usecase.ImportEventsCsvUseCase
import com.dimento.app.domain.usecase.MarkEventCompleteUseCase
import com.dimento.app.domain.usecase.ObserveGroupSummariesUseCase
import com.dimento.app.domain.usecase.ObserveGroupsUseCase
import com.dimento.app.domain.usecase.ObserveTimelineUseCase
import com.dimento.app.domain.usecase.RenameGroupUseCase
import com.dimento.app.domain.usecase.SearchMemoriesUseCase
import com.dimento.app.domain.usecase.UpdateEventUseCase
import com.dimento.app.domain.util.EventTypeResolver

class AppContainer(context: Context) {
    val appContext: Context = context
    private val database: DiMentoDatabase = Room.databaseBuilder(
        context,
        DiMentoDatabase::class.java,
        "dimento.db"
    ).fallbackToDestructiveMigration().build()

    val repository: MemoryRepository = MemoryRepositoryImpl(
        database = database,
        tokenizer = KeywordTokenizer()
    )

    val eventTypeResolver = EventTypeResolver()

    val observeGroupSummariesUseCase = ObserveGroupSummariesUseCase(repository)
    val observeGroupsUseCase = ObserveGroupsUseCase(repository)
    val observeTimelineUseCase = ObserveTimelineUseCase(repository)
    val createGroupUseCase = CreateGroupUseCase(repository)
    val renameGroupUseCase = RenameGroupUseCase(repository)
    val deleteGroupUseCase = DeleteGroupUseCase(repository)
    val getGroupUseCase = GetGroupUseCase(repository)

    val createEventUseCase = CreateEventUseCase(repository)
    val updateEventUseCase = UpdateEventUseCase(repository)
    val deleteEventUseCase = DeleteEventUseCase(repository)
    val forwardEventUseCase = ForwardEventUseCase(repository)
    val markEventCompleteUseCase = MarkEventCompleteUseCase(repository)
    val searchMemoriesUseCase = SearchMemoriesUseCase(repository)
    val ensureDefaultGroupUseCase = EnsureDefaultGroupUseCase(repository)
    val getEventsDueTodayUseCase = GetEventsDueTodayUseCase(repository, eventTypeResolver)
    val exportEventsCsvUseCase = ExportEventsCsvUseCase(repository, eventTypeResolver)
    val exportGroupEventsCsvUseCase = ExportGroupEventsCsvUseCase(repository, eventTypeResolver)
    val importEventsCsvUseCase = ImportEventsCsvUseCase(repository)
}
