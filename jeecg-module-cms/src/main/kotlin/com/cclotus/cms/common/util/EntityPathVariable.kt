package com.cclotus.cms.common.util

data class EntityPathVariable(val id:Int?, val alias:String?){

    companion object {
        fun parse(pathVariable: String): EntityPathVariable{
            return when(pathVariable[0]) {
                '_'-> EntityPathVariable(pathVariable.substring(1).toInt(), null)

                '@'-> EntityPathVariable(null, pathVariable.substring(1))

                else -> {
                    if(("^[0-9]*$").toRegex().matches(pathVariable))
                        EntityPathVariable(pathVariable.toInt(), null)
                    else
                        EntityPathVariable(null, pathVariable)
                }
            }
        }
    }

}