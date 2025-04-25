package com.plcoding.bookpedia.book.data.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

// Pass it the result class type we want to serialize to and deserialize from
object BookWorkDtoSerializer: KSerializer<BookWorkDto> {

    // give the serializer an outline of which fields we're looking for in deserializing json
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        BookWorkDto::class.simpleName.toString()) // pass it the class name
    {
        // specify the Json field elements that we care
        element<String?>("description")
    }

    // use the decoder to instruct how to decode fields we elements we care about into our Dto
    override fun deserialize(decoder: Decoder): BookWorkDto = decoder.decodeStructure(descriptor) { // initiate the decoding process
        var description: String? = null // we don't know the val yet

        while(true) {
            // try decode this whole obj by passing in the descriptor and decodeElementIndex returns
            // an index that corresponds to the element we declare in "descriptor" above.
            // The index is based on the order of elements specified in "descriptor" above
            when(val index = decodeElementIndex(descriptor)) {
                0 -> { // working with "description" field
                    val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException( // try to cast it to JsonDecoder, if cannot, we know that we did not receive json
                        "This decoder only works with Json"
                    )
                    val element = jsonDecoder.decodeJsonElement()
                    // this json element can be a list, string, obj, .etc so we have to check and process it appropriately
                    description = if (element is JsonObject) {
                        decoder.json.decodeFromJsonElement<DescriptionDto>( // fields in DescriptionDto must exist in json returned by API
                            element = element,
                            deserializer = DescriptionDto.serializer()
                        ) // this will deserialize the element into DescriptionDto
                        .value // use the "value" field of the DescriptionDto
                    } else if(element is JsonPrimitive && element.isString) {
                        element.content // use the content without doing anything
                    } else {
                        null
                    }
                }
                CompositeDecoder.DECODE_DONE -> break // stop when done deserializing
                else -> throw SerializationException("Unexpected index $index")
            }
        }

        return@decodeStructure BookWorkDto(description)
    }

    // for pushing data back to the API, but we don't do that in this app
    override fun serialize(encoder: Encoder, value: BookWorkDto) = encoder.encodeStructure(
        descriptor
    ) {
        // use the encoder to instruct how to convert our Dto into the Json format of the Api
        value.description?.let {
            encodeStringElement(descriptor, 0 , it)
        }
    }

}