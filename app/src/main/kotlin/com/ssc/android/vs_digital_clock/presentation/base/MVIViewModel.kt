package com.ssc.android.vs_digital_clock.presentation.base

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base class for ViewModel that is implemented with MVI pattern.
 *
 * The general flow starts from user interaction, which will trigger a pre-defined [Intention]
 * to be digested by ViewModel. Then, ViewModel will use the data inside given Intention to
 * start a function, which invokes operation on business logic, then use corresponding
 * [Action] to update [State].
 *
 * [Intention] -- start --> [Action] -- update --> [State] -- observer by --> UI
 *
 * [Event] is a special component in this pattern, which is used to notify UI any event
 * that only need to be handled once.
 *
 * @param defaultDispatcher CoroutineDispatcher that given ViewModel instance will use for its
 *        flow related operation.
 * @param initialState The initial state of [stateFlow].
 */
abstract class MVIViewModel<Intention, Action, State, Event>(
    private val defaultDispatcher: CoroutineDispatcher,
    initialState: State
) : ViewModel() {

    /**
     * Receive intent from the UI layer such as Activity or Fragment. Using ShareFlow since there's
     * no initial value share intent.
     */
    private val intentionFlow: MutableSharedFlow<Intention> = MutableSharedFlow()

    /**
     * Send ui state to observer. Using StateFlow, so there's always single state for
     * fragment to update its ui.
     */
    private val _stateFlow: MutableStateFlow<State> = MutableStateFlow(initialState)
    val stateFlow: StateFlow<State> = _stateFlow

    /**
     * Although [Action] is only internally inside [MVIViewModel], there are many coroutines
     * that will send action to update state. Some of them might not trigger by user actions,
     * such as observing on download status. This behavior has chance to trigger race condition if
     * we let all of them update the state by itself, so we use Flow to implement Thread
     * Confinement to prevent this potential issue.
     */
    private val actionFlow: MutableSharedFlow<Action> = MutableSharedFlow()

    /**
     * Send event to observer. Using Channel to support one-shot only event, preventing
     * single event is consumed twice.
     */
    private val _eventFlow: Channel<Event> = Channel()
    val eventFlow: Flow<Event> = _eventFlow.receiveAsFlow()

    init {
        viewModelScope.launch(defaultDispatcher) {
            intentionFlow.collect { intent ->
                onIntention(intent)
            }
        }

        viewModelScope.launch(defaultDispatcher) {
            actionFlow.collect { action ->
                _stateFlow.emit(onReduce(action))
            }
        }
    }

    fun sendIntention(intention: Intention) {
        viewModelScope.launch(defaultDispatcher) {
            intentionFlow.emit(intention)
        }
    }

    /**
     * To process the intention events observed from the UI layer.
     */
    protected abstract suspend fun onIntention(intention: Intention)

    protected suspend fun sendAction(action: Action) {
        actionFlow.emit(action)
    }

    /**
     * Define how to process given [Action] to update [State].
     * This puts restrictions on possible combination of data inside [State].
     */
    protected abstract suspend fun onReduce(action: Action): State

    protected suspend fun sendEvent(event: Event) {
        _eventFlow.send(event)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun injectState(state: State) = viewModelScope.launch(defaultDispatcher) {
        _stateFlow.emit(state)
    }
}