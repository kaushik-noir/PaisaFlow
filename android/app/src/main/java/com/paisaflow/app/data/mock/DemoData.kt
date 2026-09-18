package com.paisaflow.app.data.mock

import com.paisaflow.app.model.*

/**
 * SAMPLE DATA ONLY — fictional demo business.
 *
 * Used only for demo / fallback UI when the backend is unavailable.
 * Never present this data as real user, market, banking, or financial data.
 */
private object DemoValues {
    const val OWNER_NAME = "Sunita Devi"
    const val OWNER_FIRST_NAME = "Sunita"
    const val BUSINESS_TYPE = "Dairy"
    const val BUSINESS_NAME = "Sunita Dairy"
    const val LOCATION = "Sheikhpura"

    const val CASH_BALANCE = 100_000
    const val MONTHLY_SALES = 78_000
    const val MONTHLY_EXPENSES = 46_000

    const val REQUESTED_LOAN = 900_000
    const val SAFE_LOAN = 450_000

    const val RAMESH_DUE = 850
    const val TOTAL_DUE = 18_500

    const val CURRENT_COWS = 4
    const val EXPANSION_COWS = 6
}

/**
 * Demo onboarding / understanding data.
 */
object DemoData {

    const val OWNER_NAME = DemoValues.OWNER_NAME
    const val BUSINESS_TYPE = DemoValues.BUSINESS_TYPE

    val dairyExpansion = BrainUnderstanding(
        transcript = "“Mere paas ₹1 lakh hai, main dairy expand karna chahti hoon”",
        readBack = "“Aapne kaha ₹1 lakh apna paisa hai aur dairy badhani hai. Sahi hai?”",
        onboardingStep = 1,
        onboardingTotal = 3,
        isSampleData = true,

        facts = listOf(
            ExtractedFact(
                "Business Type",
                "डेयरी · Dairy",
                "Sheikhpura unit",
                EvidenceLabel.FACT,
            ),
            ExtractedFact(
                "Lakshya (Goal)",
                "विस्तार · Expand",
                "+6 Cows target",
                EvidenceLabel.FACT,
            ),
            ExtractedFact(
                "Apna Paisa",
                "₹1,00,000",
                "Self-investment",
                EvidenceLabel.FACT,
                isHighlight = true,
            ),
            ExtractedFact(
                "Sarkari/KCC Sahayata",
                "Loan Needed",
                "~₹2.5L to ₹3L",
                EvidenceLabel.ESTIMATE,
            ),
        ),

        nextQuestion = ClarificationQuestion(
            step = 2,
            totalSteps = 3,
            translation = "(How many cows in your shed currently?)",
            unitLabel = "Cows",
            preselected = DemoValues.CURRENT_COWS,
            options = listOf(
                ClarificationOption(2, "2 Gaay"),
                ClarificationOption(
                    DemoValues.CURRENT_COWS,
                    "4 Gaay",
                    "Sunita ji's Shed",
                ),
                ClarificationOption(6, "6 Gaay"),
            ),
        ),
    )
}

/**
 * Demo home dashboard.
 */
object DemoHome {

    val data = HomeData(
        ownerFirstName = DemoValues.OWNER_FIRST_NAME,
        businessLabel = "${DemoValues.BUSINESS_NAME} (${DemoValues.LOCATION})",
        todayLabel = "Aaj: 24 Oct",
        voiceExample = "“Kundan ne 500 diye...”",

        overdue = OverdueAlert(
            count = 1,
            daysLate = 12,
            counterparty = "Ramesh Tea Stall",
            amount = "₹850",
        ),

        metrics = listOf(
            KoshMetric(
                title = "Cash in Hand · SBI Sheikhpura A/c",
                value = "₹1,00,000",
                valueSuffix = "Safe",
                footer = "Bina chinta ke surakshit",
                evidence = EvidenceLabel.FACT,
                tone = MetricTone.PRIMARY,
                icon = MetricIcon.PAYMENTS,
                footerIcon = MetricIcon.CHECK_CIRCLE,
            ),
            KoshMetric(
                title = "Monthly Milk Sales (~32 L/din)",
                value = "₹78,000",
                valueSuffix = "+8%",
                footer = "Doodh Rate: ₹40/L",
                evidence = EvidenceLabel.FACT,
                tone = MetricTone.NEUTRAL,
                icon = MetricIcon.TRENDING_UP,
                footerIcon = MetricIcon.STOREFRONT,
            ),
            KoshMetric(
                title = "Cattle Feed (Churi/Khal)",
                value = "14 Din",
                valueSuffix = "Stock",
                footer = "Reorder Alert Soon",
                evidence = EvidenceLabel.FACT,
                tone = MetricTone.WARNING,
                icon = MetricIcon.INVENTORY,
                footerIcon = MetricIcon.ALARM,
            ),
            KoshMetric(
                title = "Kundan, Ramesh, Shyam baaki",
                value = "₹18,500",
                valueSuffix = "3 Jan",
                footer = "Kul 3 Grahak",
                evidence = EvidenceLabel.FACT,
                tone = MetricTone.SECONDARY,
                icon = MetricIcon.PENDING,
                footerIcon = MetricIcon.GROUP,
            ),
        ),

        topActions = listOf(
            TopAction(
                1,
                "Ramesh ko WhatsApp reminder bhejein",
                "₹850 baaki chukane ke liye",
                ActionKind.SEND,
            ),
            TopAction(
                2,
                "Kundan Dairy entry confirm karein",
                "Subah ka 18 Litre doodh dispatch",
                ActionKind.CHECK,
            ),
            TopAction(
                3,
                "Sheikhpura Feeds se rate confirm karein",
                "10 bori Sudha Pashu Aahar order",
                ActionKind.CALL,
            ),
        ),

        twin = TwinStatus(
            name = "Sunita Dairy Twin v1.2",
            status = "Live Synced · Agle hafte ka munafe ka anumaan tayyar",
            isLive = true,
        ),
    )
}

/**
 * Demo evidence-mode content.
 */
object DemoEvidence {

    val data = EvidenceData(
        ownerInitials = "SD",
        ownerName = "सुनीता डेयरी",
        ownerMeta = "शेखपुरा, बिहार • दुग्ध उत्पादक खाता #4102",

        // Demo UI value only. Do not treat as production verification.
        verifiedScore = "100% सत्यापित स्कोर",

        provenance = listOf(
            ProvenanceShare(
                "70% सच",
                70,
                EvidenceLabel.FACT,
            ),
            ProvenanceShare(
                "20% मंडी",
                20,
                EvidenceLabel.OBSERVATION,
            ),
            ProvenanceShare(
                "10% सूत्र",
                10,
                EvidenceLabel.ESTIMATE,
            ),
        ),

        sources = listOf(
            EvidenceSource(
                title = "आपकी खुद की आवाज़",
                evidence = EvidenceLabel.FACT,
                percent = 70,
                description = "सुनीता देवी द्वारा बोलकर दर्ज कराई गई पक्की जानकारी (Voice Confirmed Record):",

                rows = listOf(
                    KeyValue("दुधारू गाय", "4 गायें"),
                    KeyValue("पूँजी लागत", "₹1,00,000"),
                    KeyValue("मासिक बिक्री", "₹78,000"),
                ),

                checks = emptyList(),
                footer = "ऑडियो लॉग आईडी: VOX-BH-2024-8831",
                iconKind = SourceIcon.MIC,
            ),

            EvidenceSource(
                title = "शेखपुरा मंडी पड़ताल",
                evidence = EvidenceLabel.OBSERVATION,
                percent = 20,
                description = "शेखपुरा ब्लॉक मंडी रेट (अपडेट: 12 सितंबर 2024) से तुलना:",

                rows = listOf(
                    KeyValue(
                        "सरकारी फैट बेंचमार्क खरीद दर:",
                        "₹41.50 / ली.",
                    ),
                    KeyValue(
                        "आसपास की सक्रिय डेयरियां (PostGIS):",
                        "3 प्रतिस्पर्धी मैप किए गए",
                    ),
                ),

                checks = emptyList(),
                footer = null,
                iconKind = SourceIcon.STOREFRONT,
            ),

            EvidenceSource(
                title = "पक्का गणितीय फॉर्मूला",
                evidence = EvidenceLabel.ESTIMATE,
                percent = 10,
                description = "PaisaFlow Deterministic Python Engine — कोई मनगढ़ंत अंदाज़ा या AI झूठ (Zero LLM hallucination) नहीं:",

                rows = emptyList(),

                checks = listOf(
                    "Reducing Balance EMI फॉर्मूला (घटते मूलधन पर ब्याज)",
                    "4-स्ट्रैस शॉक मैट्रिक्स (सूखा, बीमारी, चारे का भाव वृद्धि)",
                ),

                footer = null,
                iconKind = SourceIcon.TERMINAL,
            ),
        ),

        explanation = SimpleExplanation(
            question = "\"EMI और Cash Buffer का असल मतलब क्या है?\"",
            metaphorTitle = "दूध की बाल्टी का नियम (रोज़ाना 8 बाल्टी उत्पादन)",
            metaphorSubtitle = "देखें हर बाल्टी का दूध कहाँ जाता है:",

            shares = listOf(
                BucketShare(
                    3,
                    "चारा-दवा",
                    MetricTone.WARNING,
                ),
                BucketShare(
                    3,
                    "बैंक EMI",
                    MetricTone.PRIMARY,
                ),
                BucketShare(
                    2,
                    "बचत बफ़र",
                    MetricTone.NEUTRAL,
                ),
            ),

            plainTalk = listOf(
                "EMI का मतलब है हर महीने दूध बेचकर बैंक को लौटाई जाने वाली पक्की क़िस्त।",
                "अगर जाड़े या बीमारी में दूध 8 बाल्टी से घटकर 5 बाल्टी हो जाए, तो बची हुई 2 बाल्टी से घर का खर्च नहीं चलेगा। इसीलिए 'बफ़र' जरूरी है ताकि आपकी जेब खाली न हो!",
            ),
        ),

        story = FieldStory(
            person = "कमला ताई, बरबीघा",
            quote = "\"जब मेरी एक गाय बीमार हुई, तो इसी 2 बाल्टी बफ़र ने मेरी क़िस्त भरी और मुझे महाजन के पास नहीं जाना पड़ा।\"",
        ),
    )
}

/**
 * Demo Hindi home screen.
 */
object DemoHomeHi {

    val data = HiHomeData(
        businessName = "सुनीता डेयरी",
        blockChip = "शेखपुरा",
        liveLine = "कोष Live · सुरक्षित खाता",
        greeting = "नमस्ते सुनीता जी! 👋",
        dateLine = "आज: 24 अक्टूबर 2026 • शेखपुरा ब्लॉक",
        question = "आज व्यापार में क्या हिसाब देखना है?",

        heroTitle = "बोलिए और हिसाब दर्ज कीजिए",
        heroSubtitle = "\"कुंदन ने 500 रुपये दिए\" या \"आज का दूध लिखो\" — बस माइक दबाकर बोलें।",
        readyLine = "सुनने के लिए तैयार: \"रमेश का बकाया कितना है?\"",

        quickPrompts = listOf(
            "💬 \"दूध का हिसाब लिखो\"",
            "⚡ \"लोन का रिस्क बताओ\"",
            "🥛 \"रमेश का बाकी\"",
        ),

        alertDays = "12 दिन बकाया",
        alertName = "रमेश टी स्टॉल",
        alertLine = "₹850 बाकी है · तगादा संदेश तैयार है",

        tiles = listOf(
            HiTile(
                "हाथ में रोकड़ (Safe)",
                "₹1,00,000",
                "SBI शेखपुरा • सुरक्षित बचत",
                EvidenceLabel.FACT,
                MetricTone.PRIMARY,
                MetricIcon.PAYMENTS,
            ),
            HiTile(
                "मासिक बिक्री (दूध)",
                "₹78,000",
                "↑ +8% पिछले महीने से",
                EvidenceLabel.FACT,
                MetricTone.NEUTRAL,
                MetricIcon.TRENDING_UP,
            ),
            HiTile(
                "सुधा दाना चूरी स्टॉक",
                "14 दिन का",
                "⚠️ 3 दिन में ऑर्डर दें",
                EvidenceLabel.ESTIMATE,
                MetricTone.WARNING,
                MetricIcon.INVENTORY,
            ),
            HiTile(
                "मार्केट का बाकी उधार",
                "₹18,500",
                "3 ग्राहक (रमेश, कुंदन, श्याम)",
                EvidenceLabel.FACT,
                MetricTone.SECONDARY,
                MetricIcon.PENDING,
            ),
        ),

        twinName = "सुनीता डेयरी डिजिटल ट्विन v1.2",
        healthLine = "व्यापार की सेहत: सुरक्षित",
        runwayText = "84 दिन (लगभग 3 महीने)",
        seasonTitle = "सर्दियों का मौसम अनुमान",
        seasonBody = "दिसंबर में हरे चारे के दाम 12% बढ़ सकते हैं। अभी अग्रिम स्टॉक का विकल्प देखें।",

        // Demo UI value only.
        modelAccuracy = "99.2% मॉडल एक्यूरेसी",

        tasks = listOf(
            HiTask(
                "रमेश टी स्टॉल को याद दिलाएं",
                "₹850 बकाया (12 दिन) • ड्राफ्ट तैयार",
                "भेजें",
                ActionKind.SEND,
                true,
            ),
            HiTask(
                "कुंदन डेयरी का 18L दूध दर्ज करें",
                "सुबह का कोटा • ₹720 का हिसाब",
                "पक्का करें",
                ActionKind.CHECK,
                false,
            ),
            HiTask(
                "शेखपुरा मंडी रेट चेक करें",
                "आज का भाव ₹41.50/L (+₹1.50 ज़्यादा)",
                "देखें",
                ActionKind.CALL,
                false,
            ),
        ),

        lastVoiceEntry = "\"श्याम ग्वाला से 2 बोरी खली ली\"",
    )
}

/**
 * Demo loan simulation.
 */
object DemoSimulation {

    /**
     * Keep the safe plan separately so DemoActions does not need unsafe (!!)
     * nullable access through LoanSimResponse.safePlan.
     */
    val safePlan = SafePlanOut(
        450_000,
        6,
        9_784,
        376,
        29_352,
        "₹9,00,000 के बदले ₹4,50,000 का लोन लें (+6 गायें) और ₹29,352 (3 महीने की किस्त) का सुरक्षा बफ़र अलग खाते में रखें। इससे सर्दियों में भी ₹376 की शुद्ध बचत बनी रहेगी।",
    )

    val nineLakh = LoanSimResponse(
        question = "“अगर मैं ₹9,00,000 का लोन लूँ तो क्या होगा?”",
        emi = 19_568,
        totalInterest = 274_080,
        safeLimit = 450_000,
        baseSurplusAfterEmi = 12_432,
        winterSurplusAfterEmi = -9_408,
        winterDeficit3Months = 28_224,

        scenarios = listOf(
            ScenarioOut(
                "base",
                "सामान्य स्थिति (Base Case)",
                null,
                "TIGHT",
                78_000,
                46_000,
                32_000,
                19_568,
                12_432,
                61,
            ),

            ScenarioOut(
                "sales",
                "दूध भाव में -20% गिरावट",
                "Sales & Rate Shock",
                "DEFICIT",
                62_400,
                46_000,
                16_400,
                19_568,
                -3_168,
                119,
                "डेयरी संकलन केंद्र द्वारा भाव घटाने पर",
            ),

            ScenarioOut(
                "cost",
                "चारा व दाना +15% महंगा",
                "Input Cost Shock",
                "TIGHT",
                78_000,
                52_900,
                25_100,
                19_568,
                5_532,
                78,
                "कमज़ोर सुरक्षा बफ़र",
            ),

            ScenarioOut(
                "winter",
                "सर्दियों की मंदी -28%",
                "Winter Lean Period (Dec-Feb)",
                "DEFICIT",
                56_160,
                46_000,
                10_160,
                19_568,
                -9_408,
                193,
                "सर्दियों के 3 महीनों में कुल ₹28,224 की उधारी चढ़ने का जोखिम",
            ),
        ),

        safePlan = safePlan,

        note = "Yeh anumaan hai — aapke bataye aankdon aur assumptions par. Loan approval, munafa ya bhavishya ki guarantee nahi.",
    )
}

/**
 * Demo action-layer content.
 */
object DemoActions {

    val data = ActionsData(
        pendingCount = 3,

        collection = CollectionAction(
            customer = "रमेश टी स्टॉल को तगादा",
            customerMeta = "12 सितम्बर की शाम दूध सप्लाई (42 लीटर)",
            address = "रमेश टी स्टॉल (स्टेशन रोड)",
            relationship = "दुकानदार से 8 महीने का भरोसेमंद सम्बंध",
            phone = "+919800000000",
            amount = 850,
            daysOverdue = 12,

            why = "पिछले 12 दिनों से भुगतान नहीं हुआ। आज याद दिलाने से कल सुबह का हरा चारा व खली ख़रीदने में नकदी की तंगी नहीं होगी।",

            draft = "नमस्ते रमेश जी, सुनीता डेयरी का ₹850 का पिछला बकाया चल रहा है। आज शाम तक UPI या नकद कराने की कृपा करें ताकि चारे का भुगतान हो सके। धन्यवाद!",
        ),

        reorder = ReorderAction(
            title = "सुधा पशु आहार स्टॉक री-ऑर्डर",
            supplier = "शेखपुरा एग्रो फीड्स प्राइवेट लिमिटेड",
            supplierPhone = "+91988778930",
            amount = 8_400,
            quantityLabel = "10 बोरी चूरी-खली",
            stockPercent = 22,
            daysLeft = 3.5,
            availableBags = 2,
            minSafeBags = 8,
            product = "सुधा स्पेशल गोल्ड (50kg x 10)",
            priceNote = "28 अक्टूबर से ₹120/बोरी बढ़ने का अनुमान",

            insight = "मंडी में खली की आवक घटने से दाम ₹120/बोरी बढ़ने वाले हैं। आज 10 बोरी बुक करने पर सीधे ₹1,200 की पक्की बचत होगी।",

            savings = 1_200,
        ),

        finance = FinanceAction(
            title = "₹9 लाख के बदले ₹4.5 लाख वाला सुरक्षित लोन चुनें",
            scheme = "बैंक डेयरी विस्तार योजना आवेदन",
            unsafeLoan = 900_000,
            unsafeEmi = DemoSimulation.nineLakh.emi,

            // No unsafe !! operator here.
            safeLoan = DemoSimulation.safePlan.loan,
            safeEmi = DemoSimulation.safePlan.emi,
            winterBuffer = DemoSimulation.safePlan.winterSurplusAfterEmi,

            insight = "छोटे लोन पर सर्दियों में दूध उत्पादन घटने पर भी बचत बनी रहेगी और 3 महीने की किस्त का आपातकालीन बफ़र पूरी तरह सुरक्षित रहेगा।",
        ),
    )
}
