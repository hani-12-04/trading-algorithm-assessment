package codingblackfemales.dictionary;

import messages.marketdata.*;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Test;
// import uk.co.real_logic.sbe.ir.generated.MessageHeaderDecoder;
// import uk.co.real_logic.sbe.ir.generated.MessageHeaderEncoder;
// import messages.marketdata.BookUpdateEncoder;
// import messages.marketdata.BookUpdateDecoder;


import java.nio.ByteBuffer;

// import static com.sun.org.apache.xml.internal.serializer.utils.Utils.messages;


public class EncodingDecodingTest {

    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final BookUpdateEncoder encoder = new BookUpdateEncoder();
    private final BookUpdateDecoder decoder = new BookUpdateDecoder();

    @Test
    public void encodingDecoding() throws Exception {

        //Allocate memory and an unsafe buffer to act as a destination for the data
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        //write the encoded output to the direct buffer
        encoder.wrapAndApplyHeader(directBuffer, 0, headerEncoder);

        System.out.println(encoder.sbeTemplateId());

        //set the fields to desired values
        encoder.venue(Venue.XLON);
        encoder.instrumentId(123L);

        encoder.askBookCount(3)
                .next().price(100L).size(101L)
                .next().price(90L).size(200L)
                .next().price(80L).size(300);

        encoder.bidBookCount(3)
                .next().price(110L).size(100)
                .next().price(210L).size(200)
                .next().price(310L).size(300);

        encoder.instrumentStatus(InstrumentStatus.CONTINUOUS);
        encoder.source(Source.STREAM);

        int encodedLength = MessageHeaderEncoder.ENCODED_LENGTH + encoder.encodedLength();

        System.out.println("Encoded:" + encoder);

        int bufferOffset = 0;
        headerDecoder.wrap(directBuffer, bufferOffset);

        final int schema = headerDecoder.schemaId();

        if (schema != BookUpdateEncoder.SCHEMA_ID)
        {
            throw new IllegalStateException("Template ids do not match");
        }

        final int actingBlockLength = headerDecoder.blockLength();
        final int actingVersion = headerDecoder.version();

        bufferOffset += headerDecoder.encodedLength();

        decoder.wrap(directBuffer, bufferOffset, actingBlockLength, actingVersion);

        final var bidBookDecoder = decoder.bidBook().next();

        Assert.assertEquals(123L, decoder.instrumentId());
        Assert.assertEquals(InstrumentStatus.CONTINUOUS, decoder.instrumentStatus());
        Assert.assertEquals(101L, bidBookDecoder.size());
        Assert.assertEquals(100L, bidBookDecoder.price());
        //Assert.assertEquals(100L, decoder.askBook().next().size());
    }

}
