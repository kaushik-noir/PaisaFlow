package com.paisaflow.app.i18n

import com.paisaflow.app.model.ActionsData
import com.paisaflow.app.model.CollectionAction
import com.paisaflow.app.model.FinanceAction
import com.paisaflow.app.model.ReorderAction

/**
 * Fictional SAMPLE content for the Action Layer.
 *
 * Important:
 * - This is demo content only.
 * - It must never be presented as real user, bank, supplier, or market data.
 * - Bhojpuri / Magahi / Maithili currently fall back to Hindi until
 *   reviewed native-language copy is available.
 */
object ActionsContent {

    private object DemoValues {
        const val PENDING_COUNT = 3
        const val AUDIO_DURATION_HI = "1:40 मिनट"
        const val AUDIO_DURATION_EN = "1:40 min"

        const val COLLECTION_AMOUNT = "₹850"
        const val COLLECTION_PHONE = "+919800000000"

        const val REORDER_AMOUNT = "₹8,400"
        const val REORDER_SAVING = "₹1,200"
        const val REORDER_PHONE = "+919876543210"

        const val UNSAFE_LOAN_LABEL_HI = "₹9 लाख लोन (जोखिम भरा)"
        const val SAFE_LOAN_LABEL_HI = "₹4.5 लाख लोन (मुनीम अनुशंसित)"

        const val UNSAFE_EMI = "EMI ₹19,568"
        const val SAFE_EMI = "EMI ₹9,784"
    }

    /**
     * Base Hindi demo content.
     *
     * Hinglish and English versions are derived with copy(), which keeps
     * shared demo numbers and untouched fields consistent.
     */
    private val hindi = ActionsData(
        pendingCount = DemoValues.PENDING_COUNT,
        audioDuration = DemoValues.AUDIO_DURATION_HI,

        collection = CollectionAction(
            title = "रमेश टी स्टॉल को तगादा",
            place = "शेखपुरा चौक, निकट बस स्टैंड",
            amount = DemoValues.COLLECTION_AMOUNT,
            context = "12 सितम्बर शाम का 42 लीटर दूध का भुगतान लंबित है।",
            why = "आज शाम तक यह मिलने पर कल सुबह चूरी-खली व हरा चारा नकद ले पाएँगी, साहूकार से नया कर्ज़ नहीं लेना पड़ेगा।",
            customer = "रमेश टी स्टॉल (स्टेशन रोड)",
            relationship = "दुकानदार से 8 महीने का भरोसेमंद सम्बंध",
            phone = DemoValues.COLLECTION_PHONE,
            draft = "नमस्ते रमेश जी, सुनीता डेयरी का ₹850 का पिछला बकाया चल रहा है। कृपया आज शाम तक UPI या नकद कराने की कृपा करें ताकि पशु आहार खरीदा जा सके। धन्यवाद!",
        ),

        reorder = ReorderAction(
            title = "सुधा पशु आहार स्टॉक री-ऑर्डर",
            supplier = "शेखपुरा फीड्स डिपो • 10 बोरी चूरी-खली",
            amount = DemoValues.REORDER_AMOUNT,
            saving = "${DemoValues.REORDER_SAVING} की बचत",
            stockPct = 22,
            stockDays = 3.5,
            availableBags = 2,
            minBags = 8,
            product = "सुधा स्पेशल गोल्ड (50kg x 10)",
            priceNote = "28 अक्टूबर से ₹120/बोरी बढ़ने का अनुमान",
            insight = "मंडी में आवक घटने से 28 अक्टूबर से ₹120/बोरी भाव बढ़ने का अनुमान है। आज 10 बोरी बुक करने पर सीधे ₹1,200 की पक्की बचत होगी।",
            phone = DemoValues.REORDER_PHONE,
        ),

        finance = FinanceAction(
            title = "₹9 लाख के बदले ₹4.5 लाख वाला सुरक्षित लोन चुनें",
            scheme = "भैंस विस्तार योजना (शेखपुरा ग्रामीण बैंक प्रस्ताव)",
            unsafeLabel = DemoValues.UNSAFE_LOAN_LABEL_HI,
            unsafeEmi = DemoValues.UNSAFE_EMI,
            unsafeNote = "सर्दियों में दूध उत्पादन घटने पर -₹9,408/माह नकद घाटा और बैंक डिफ़ॉल्ट का गंभीर खतरा।",
            safeLabel = DemoValues.SAFE_LOAN_LABEL_HI,
            safeEmi = DemoValues.SAFE_EMI,
            safeNote = "मंदे मौसम में भी बचत बनी रहे + हाथ में ₹29,352 सुरक्षा बफ़र (3 किस्त) हमेशा सुरक्षित।",
        ),
    )

    /**
     * Hinglish copy.
     */
    private val hinglish = hindi.copy(
        audioDuration = DemoValues.AUDIO_DURATION_EN,

        collection = hindi.collection.copy(
            title = "Ramesh Tea Stall ko taqada",
            place = "Sheikhpura Chowk, bus stand ke paas",
            context = "12 September shaam ka 42 litre doodh ka payment pending hai.",
            why = "Aaj shaam tak mil jaye toh kal subah churi-khali aur hara chara nakad le paayengi, sahukar se naya karz nahi lena padega.",
            customer = "Ramesh Tea Stall (Station Road)",
            relationship = "8 mahine ka bharosemand sambandh",
            draft = "Namaste Ramesh ji, Sunita Dairy ka ₹850 ka pichhla baaki chal raha hai. Kripya aaj shaam tak UPI ya nakad de dein taaki pashu aahar khareeda ja sake. Dhanyavaad!",
        ),

        reorder = hindi.reorder.copy(
            title = "Sudha pashu aahar stock re-order",
            supplier = "Sheikhpura Feeds Depot • 10 bori churi-khali",
            saving = "${DemoValues.REORDER_SAVING} ki bachat",
            product = "Sudha Special Gold (50kg x 10)",
            priceNote = "28 October se ₹120/bori badhne ka anumaan",
            insight = "Mandi mein aavak ghatne se 28 October se ₹120/bori bhaav badhne ka anumaan hai. Aaj 10 bori book karne par seedhe ₹1,200 ki pakki bachat hogi.",
        ),

        finance = hindi.finance.copy(
            title = "₹9 lakh ke badle ₹4.5 lakh wala surakshit loan chunein",
            scheme = "Bhains vistaar yojana (Sheikhpura Gramin Bank prastav)",
            unsafeLabel = "₹9 lakh loan (jokhim bhara)",
            unsafeNote = "Sardi mein doodh ghatne par -₹9,408/maah nakad ghata aur bank default ka gambhir khatra.",
            safeLabel = "₹4.5 lakh loan (Munim ki salah)",
            safeNote = "Mande mausam mein bhi bachat bani rahe + haath mein ₹29,352 suraksha buffer (3 kist) hamesha surakshit.",
        ),
    )

    /**
     * English copy.
     */
    private val english = hindi.copy(
        audioDuration = DemoValues.AUDIO_DURATION_EN,

        collection = hindi.collection.copy(
            title = "Collect from Ramesh Tea Stall",
            place = "Sheikhpura Chowk, near bus stand",
            context = "Payment for 42 litres of milk supplied on the evening of 12 September is pending.",
            why = "If it comes in by tonight, you can buy churi-khali and green fodder for cash tomorrow morning instead of borrowing from the moneylender.",
            customer = "Ramesh Tea Stall (Station Road)",
            relationship = "8-month trusted relationship",
            draft = "Namaste Ramesh ji, Sunita Dairy's earlier dues of ₹850 are pending. Kindly pay by UPI or cash by this evening so cattle feed can be bought. Thank you!",
        ),

        reorder = hindi.reorder.copy(
            title = "Re-order Sudha cattle feed stock",
            supplier = "Sheikhpura Feeds Depot • 10 bags churi-khali",
            saving = "Saves ${DemoValues.REORDER_SAVING}",
            product = "Sudha Special Gold (50kg x 10)",
            priceNote = "Expected to rise ₹120/bag from 28 October",
            insight = "With lower arrivals in the mandi, prices are expected to rise ₹120/bag from 28 October. Booking 10 bags today saves a sure ₹1,200.",
        ),

        finance = hindi.finance.copy(
            title = "Choose the safer ₹4.5 lakh loan instead of ₹9 lakh",
            scheme = "Buffalo expansion plan (Sheikhpura Gramin Bank proposal)",
            unsafeLabel = "₹9 lakh loan (higher risk)",
            unsafeNote = "When winter milk output drops: −₹9,408/month cash deficit and a serious risk of bank default.",
            safeLabel = "₹4.5 lakh loan (demo recommendation)",
            safeNote = "Savings remain positive in the lean-season demo scenario, with a ₹29,352 safety buffer equal to 3 instalments.",
        ),
    )

    /**
     * Returns Action Layer demo content for the selected app language.
     *
     * Bhojpuri / Magahi / Maithili and any language without reviewed copy
     * currently use Hindi as the safe fallback.
     */
    fun forLang(
        lang: AppLanguage,
    ): ActionsData {
        return when (lang) {
            AppLanguage.HINGLISH -> hinglish
            AppLanguage.EN -> english
            else -> hindi
        }
    }
}
