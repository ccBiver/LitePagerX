package com.ccBiver.litepagerx

class ViewList<E> : ArrayList<E>() {

    //添加view至头部
    fun addViewByHead(value:E){
        add(0,value)
    }

    //添加view至尾部
    fun addViewByTail(value: E){
        add(value)
    }

    //添加view至头部并弹出尾部
    fun addViewByHeadAndPollTail(value: E):E{
        add(0,value)
        return get(lastIndex).also { removeAt(lastIndex) }
    }

    //添加view至尾部并弹出头部
    fun addViewByTailAndPollHead(value: E):E{
        add(value)
        return get(0).also { removeAt(0) }
    }

    //获取头部
    fun getViewByHead():E{
        return first()
    }

    //获取尾部
    fun getViewByTail():E{
        return last()
    }

    //获取头部第一个并删除
    fun getViewByHeadAndRemove():E{
        return if(isNullOrEmpty())throw NullPointerException("列表为空") else first().also {
            removeAt(0)
        }
    }

    //获取尾部第一个并删除
    fun getViewByTailAndRemove():E{
        return if(isNullOrEmpty())throw NullPointerException("列表为空") else last().also {
            removeAt(lastIndex)
        }
    }
}