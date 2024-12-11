package com.stkj.cashier.bean

class MessageEventBean {
    var type: Int
    var content: String? = null
    var ext: String? = null
    var status = 0
    var obj: Any? = null
    var realPayMoney:String? = null

    constructor(type: Int) {
        this.type = type
    }

    constructor(type: Int, content: String?) {
        this.type = type
        this.content = content
    }

    constructor(type: Int, obj: Any?) {
        this.type = type
        this.obj = obj
    }

    constructor(type: Int, content: String?, ext: String?) {
        this.type = type
        this.content = content
        this.ext = ext
    }

    constructor(type: Int, content: String?, obj: Any?) {
        this.type = type
        this.content = content
        this.obj = obj
    }

    constructor(type: Int, content: String?, status: Int, obj: Any?) {
        this.type = type
        this.content = content
        this.status = status
        this.obj = obj
    }

    constructor(type: Int, content: String?, ext: String?, obj: Any?) {
        this.type = type
        this.content = content
        this.ext = ext
        this.obj = obj
    }
}