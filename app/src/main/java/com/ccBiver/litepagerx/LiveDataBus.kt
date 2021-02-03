package com.ccBiver.litepagerx

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlin.collections.HashMap

object LiveDataBus {
    private val bus: HashMap<String, BusMutableLiveData<*>> = HashMap()

    fun<T> with(key: String, type: Class<T>):MutableLiveData<T>{
        if(!bus.containsKey(key)){
            bus[key] = BusMutableLiveData<T>()
        }
        return bus[key] as MutableLiveData<T>
    }

    fun with(key:String):MutableLiveData<Any>{
        return with(key, Any::class.java)
    }

    private class BusMutableLiveData<T> : MutableLiveData<T>() {
        private val observerMap: HashMap<Observer<in T>, Observer<*>> = HashMap()

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, observer)
            try{
                hook(observer)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        override fun observeForever(observer: Observer<in T>) {
            if (!observerMap.containsKey(observer)) {
                observerMap[observer] = ObserverWrapper(observer)
            }
            super.observeForever(observer)
        }

        override fun removeObserver(observer: Observer<in T>) {
            super.removeObserver(
                (if (observerMap.containsKey(observer)) {
                    observerMap.remove(observer)
                } else {
                    observer
                }) as Observer<in T>
            )
        }

        private fun hook(observer: Observer<in T>) {
            //get wrapper's version
            val classLiveData = LiveData::class.java
            val fieldObservers =
                classLiveData.getDeclaredField("mObservers")
            fieldObservers.isAccessible = true
            val objectObservers = fieldObservers[this]
            val classObservers: Class<*> = objectObservers.javaClass
            val methodGet = classObservers.getDeclaredMethod(
                "get",
                Any::class.java
            )
            methodGet.isAccessible = true
            val objectWrapperEntry = methodGet.invoke(objectObservers, observer)
            var objectWrapper: Any? = null
            if (objectWrapperEntry is Map.Entry<*, *>) {
                objectWrapper = objectWrapperEntry.value
            }
            if (objectWrapper == null) {
                throw NullPointerException("Wrapper can not be bull!")
            }
            val classObserverWrapper: Class<*>? = objectWrapper.javaClass.superclass
            val fieldLastVersion =
                classObserverWrapper!!.getDeclaredField("mLastVersion")
            fieldLastVersion.isAccessible = true
            //get livedata's version
            val fieldVersion = classLiveData.getDeclaredField("mVersion")
            fieldVersion.isAccessible = true
            val objectVersion = fieldVersion[this]
            //set wrapper's version
            fieldLastVersion[objectWrapper] = objectVersion
        }
    }

    private class ObserverWrapper<T> constructor(val observer: Observer<T>) : Observer<T> {

        override fun onChanged(t: T) {
            if(isCallOnObserver()){
                return
            }
            observer.onChanged(t)
        }

        private fun isCallOnObserver():Boolean {
            val stackTrace = Thread.currentThread().stackTrace
            if (stackTrace.isNotEmpty()) {
                stackTrace.forEach {
                    if ("android.arch.lifecycle.LiveData" == it.className
                        && "observeForever" == it.methodName
                    ) {
                        return true
                    }
                }
            }
            return false
        }
    }
}