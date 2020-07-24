package com.example.mvibase

interface IAction
/**
 * actions concern UI changes (navigation, actions on mainthread)
 */
interface UIAction : IAction

/**
 * Actions should be handled on IO thread
 */
interface IOAction : IAction

/**
 * Actions should be handled in Computation thread
 */
interface ComputeAction : IAction