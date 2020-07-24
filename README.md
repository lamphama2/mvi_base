# mvi_base
Base classes for MVI architecture


This model is inspired by Redux and Flux model. Some core actors stay the same as in Redux but some of their behaviors may change adapting the requirements of Android environnement. The idea, as Flux's creation's purpose, revolves around a **strict unidirectional data flow**. 

![Image of diagram class](resources/diagram_class.png)

The main actors in the model are:

- **View**: this is always the start point of flow. It dispatches an _Action_ to _Store_. When _Store_ sends back output, _View_ update UI within _render()_ method. This method is also the end point of the flow. So a complete and close flow is started by a _dispatch()_ and finished by _render()_. In this part, there are 2 smaller elements, _View_ and _ViewModel_. Different from MVVM model, _ViewModel_ here is no more like a state manager (thank to the principe of _ViewModel_ from _Jetpack_) for _View_. It does not contain any logic business. 
    - Codes in _ViewModel_ are less _Android_ than in _View_, it just helps to hold and to mutate the state.

    - Typically, _View_ performs every actions related to UI. On top of that, it helps to bind data from _ViewModel_ to UI

    - Relation between _View_ - _ViewModel_ is : `n..1`

- **Action** : defines what will happen. Different from Redux, Action carries and transfers current state

- **Store** : a singleton that contains _reduce()_ function. Not like _Store_ in redux, this _Store_ does not hold states of application but it acts like:

    - `Reducer` carrier
    
    - Point communication between components (fragments, activity)

- **Reducer** : a pure function that takes _Action_ as input and return _State_. This is where all core logics happen, so to keep it maintenable and testable, there are things that should never happen in _reduce()_ function:

    - Mutate the arguments
    
    - Perform side effect actions.

- **State**: updated state container which will be sent to View to handle

# Structure of base classes

To keep an certain abstraction level, some base classes are created to perform the relation between components in the architecture.

- **BaseView** (BaseFragment, BaseActivity):  Each View is defined with 3 dependencies: type of Action, type of State and type Store. As a typical element in _View_ part, _BaseView_ provides _dispatch()_ and _render()_ method. It contains a _Store_ reference and probably a _ViewModel_ one (or shareViewModel) corresponding (if there is state management in this view). This _ViewModel_ will be passed to xml to benefit the feature of _DataBinding_.

- **BaseViewModel**: this is a Lifecyle observer because its lifecycle depends on the lifecyle of its _View_ it is attached to. Since this component is also in _View_ part, so _BaseViewModel_ provides also _dispatch()_ and _render()_ and hold a reference of _Store_. On top of that, it may (or _should_ because purpose of its existence is to manage states of View) contain states.

- **BaseStore**: except some core elements like _reduce()_ method mentionned above, _BaseStore_ contains a *Dispatcher* which does some pre-setup (like provide the executor for action) for every action dispatched. Moreover, _BaseStore_ provides methods to *subscribe* and *unsubscribe* state changes. _BaseStore_ is a singleton at the level of module (I'm not sure wheather _"module"_ describes exactly what I mean but like a process that can work isolately from other. For instance, in my projects, authentification (login/enregister/password foggotten etc..) is a process, main flow is another one, payment is too etc.., and each module contain only one Activity so the Store depends on the lifecycle of that container Activity).

- **reduce()**: knowing that I'm currently using RxJava for my threading and reactivité handling, so `reduce()` is a chain of Rx operators starting from an _Observable<Action>_ and returning an _Observable<State>_
  
- **Dispatcher**: filter the action received from _View_ and get ready environnement for executing that action.

# Flow (what you have to really do after having all base classes ready)

To start a flow:

- Describe what you want to do by defining an Action class with the required data input

- From either _BaseView_ or _BaseViewModel_, `dispatch()` that action

- Handle action in `reduce()` method of _Store_ while defining an _State_ returned.

- Update UI in `render()` _View_ or update state in  `render()` of _ViewModel_

- And then, you are good

# Analyse
#### 1. Avantage