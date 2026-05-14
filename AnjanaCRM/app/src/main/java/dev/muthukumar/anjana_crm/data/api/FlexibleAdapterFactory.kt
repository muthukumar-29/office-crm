package dev.muthukumar.anjana_crm.data.api

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import dev.muthukumar.anjana_crm.data.model.ApiListResponse
import dev.muthukumar.anjana_crm.data.model.ApiResponse

/**
 * Handles backends that sometimes return a raw JSON array [] instead of
 * the expected wrapper object {"success":true,"data":[...]}.
 *
 * When a raw array is received for ApiListResponse or ApiResponse, it is
 * automatically wrapped so the rest of the app code works unchanged.
 */
class FlexibleAdapterFactory : TypeAdapterFactory {

    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val raw = type.rawType
        if (raw != ApiListResponse::class.java && raw != ApiResponse::class.java) return null

        val delegate        = gson.getDelegateAdapter(this, type)
        val elementAdapter  = gson.getAdapter(JsonElement::class.java)

        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter, value: T?) = delegate.write(out, value)

            override fun read(`in`: JsonReader): T? {
                val json = elementAdapter.read(`in`) ?: return null

                // Happy path: backend sent the expected wrapper object
                if (json.isJsonObject) {
                    val obj = json.asJsonObject
                    // Already wrapped properly
                    if (obj.has("data") || obj.has("success")) {
                        return delegate.fromJsonTree(json)
                    }
                    // Object with no wrapper → treat the whole object as "data"
                    val wrapped = JsonObject()
                    wrapped.addProperty("success", true)
                    wrapped.add("data", json)
                    return delegate.fromJsonTree(wrapped)
                }

                // Backend returned a raw array [] — wrap it
                if (json.isJsonArray) {
                    val wrapped = JsonObject()
                    wrapped.addProperty("success", true)
                    wrapped.add("data", json)
                    return delegate.fromJsonTree(wrapped)
                }

                return delegate.fromJsonTree(json)
            }
        }
    }
}
