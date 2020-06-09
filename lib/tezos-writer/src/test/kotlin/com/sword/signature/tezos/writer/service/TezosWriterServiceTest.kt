package com.sword.signature.tezos.writer.service

import com.sword.signature.tezos.writer.configuration.TestConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [TestConfiguration::class])
@ActiveProfiles("test")
class TezosWriterServiceTest(
    @Autowired private val tezosWriterService: TezosWriterService
) {

    @ParameterizedTest
    @MethodSource("tezosAccounts")
    fun retrieveIdentityTest(
        name: String,
        publicKey: String,
        secretKey: String,
        publicAddress: String,
        privateKey: String
    ) {
        val tezosIdentity = tezosWriterService.retrieveIdentity(publicKey, secretKey)
        assertEquals(publicAddress, tezosIdentity.publicAddress.value)
        assertEquals(privateKey, tezosIdentity.privateKey.value)
    }


    companion object {
        @JvmStatic
        fun tezosAccounts() = listOf(
            Arguments.of(
                "alice",
                "edpkvGfYw3LyB1UcCahKQk4rF2tvbMUk8GFiTuMjL75uGXrpvKXhjn",
                "edsk3QoqBuvdamxouPhin7swCvkQNgq4jP5KZPbwWNnwdZpSpJiEbq",
                "tz1VSUr8wwNhLAzempoch5d6hLRiTh8Cjcjb",
                "edskRpm2mUhvoUjHjXgMoDRxMKhtKfww1ixmWiHCWhHuMEEbGzdnz8Ks4vgarKDtxok7HmrEo1JzkXkdkvyw7Rtw6BNtSd7MJ7"
            ),
            Arguments.of(
                "bob",
                "edpkurPsQ8eUApnLUJ9ZPDvu98E8VNj4KtJa1aZr16Cr5ow5VHKnz4",
                "edsk3RFfvaFaxbHx8BMtEW1rKQcPtDML3LXjNqMNLCzC3wLC1bWbAt",
                "tz1aSkwEot3L2kmUvcoxzjMomb9mvBNuzFK6",
                "edskRpthz6CnXsmvknNHAozBJGax6e5Hj9q2bK3j8u4XJRgvAgkGHTvy9Q8ksNsbtMmU2JzsC2wXD3BhMQx346QxWzjDVsTZzG"
            )
        )
    }


}