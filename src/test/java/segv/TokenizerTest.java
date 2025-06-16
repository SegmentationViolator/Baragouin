package segv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TokenizerTest {
    @ParameterizedTest
    @MethodSource("provideParameters")
    void tokenizerTest(String text, String[] tokens) {
        var tokenizer = new Tokenizer(text);

        for (int i = 0; i < tokens.length; i++) {
            assertEquals(tokens[i], tokenizer.nextToken());
        }

        assertEquals(null, tokenizer.nextToken());
    }

    static Stream<Arguments> provideParameters() {
        // @formatter:off
        return Stream.of(
            Arguments.of(
                "Waltz, bad nymph, for quick jigs vex.",
                new String[]{ "Waltz", ",", "bad", "nymph", ",", "for", "quick", "jigs", "vex", "." }
            ),
            Arguments.of(
                "نص حكيم له سر قاطع وذو شأن عظيم مكتوب على ثوب أخضر ومغلف بجلد أزرق",
                new String[]{ "نص", "حكيم", "له", "سر", "قاطع", "وذو", "شأن", "عظيم", "مكتوب", "على", "ثوب", "أخضر", "ومغلف", "بجلد", "أزرق" }
            ),
            Arguments.of(
                "သီဟိုဠ်မှ ဉာဏ်ကြီးရှင်သည် အာယုဝဍ္ဎနဆေးညွှနစက ဇလန်ဈေးဘေးဗာဒံပင်ထက် အဓိဋ္ဌာန်လျက် ဂဃနဏဖတ်ခဲ့သည်။",
                new String[]{ "သီဟိုဠ်မှ", "ဉာဏ်ကြီးရှင်သည်", "အာယုဝဍ္ဎနဆေးညွှနစက", "ဇလန်ဈေးဘေးဗာဒံပင်ထက်", "အဓိဋ္ဌာန်လျက်", "ဂဃနဏဖတ်ခဲ့သည်", "။" }
            ),
            Arguments.of(
                "В чащах юга жил бы цитрус? Да, но фальшивый экземпляр!",
                new String[]{ "В", "чащах", "юга", "жил", "бы", "цитрус", "?", "Да", ",", "но", "фальшивый", "экземпляр", "!" }
            ),
            Arguments.of(
                "키스의 고유 조건은 입술끼리 만나야 하고 특별한 기술은 필요치 않다.",
                new String[]{ "키스의", "고유", "조건은", "입술끼리", "만나야", "하고", "특별한", "기술은", "필요치", "않다", "." }
            ),
            Arguments.of(
                "घटाश्च शङ्खाश्च धरन्ति तोयम् ।\nशठादिमूढा न भजन्ति सत्यम् ।\nवराहयूथानि किलन्ति पुच्छैः ।\nगडेषु झञ्झाः सबलं फणन्ते ॥",
                new String[]{ "घटाश्च", "शङ्खाश्च", "धरन्ति", "तोयम्", "।", "शठादिमूढा", "न", "भजन्ति", "सत्यम्", "।", "वराहयूथानि", "किलन्ति", "पुच्छैः", "।", "गडेषु", "झञ्झाः", "सबलं", "फणन्ते", "॥" }
            ),
            Arguments.of(
                "Can you imagine this: 100% exciting—like, utterly surreal?! 🥳✨ First, we have the key ingredients: @#^$; you know—1, 2, 3... the party starts at 7:00 PM! And don't forget to RSVP by 5/15/2025! 🤔💬",
                new String[]{ "Can", "you", "imagine", "this", ":", "100", "%", "exciting", "—", "like", ",", "utterly", "surreal", "?", "!", "🥳", "✨", "First", ",", "we", "have", "the", "key", "ingredients", ":", "@", "#", "^", "$", ";", "you", "know", "—", "1", ",", "2", ",", "3", ".", ".", ".", "the", "party", "starts", "at", "7", ":", "00", "PM", "!", "And", "don", "'", "t", "forget", "to", "RSVP", "by", "5", "/", "15", "/", "2025", "!", "🤔", "💬" }
            ),
            Arguments.of(
                "Hollow Technique: Purple (虚式「茈」/きょしき むらさき Kyoshiki・Murasaki)",
                new String[]{ "Hollow", "Technique", ":", "Purple", "(", "虚式", "「", "茈", "」", "/", "きょしき", "むらさき", "Kyoshiki", "・", "Murasaki", ")" }
            )
        );
        // @formatter:on
    }
}
