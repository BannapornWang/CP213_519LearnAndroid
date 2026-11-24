# Finora — Final Verbal Exam Study Guide (Target: 100/100)

> เอกสารเตรียมสอบปากเปล่า (Final Verbal Exam 15–20 นาที) สำหรับโปรเจกต์ **Finora**  
> อ้างอิงจากโค้ดจริงใน repo ทุกฟังก์ชันและเลขบรรทัด (ไฟล์นี้ถูก ignore ไว้ใน `.gitignore`)

---

## 0. ภาพรวมการสอบ & Rubric (100 คะแนน)

| ช่วง | เวลา | ทำอะไร | เกณฑ์ที่วัด | คะแนน |
| :--- | :---: | :--- | :--- | :---: |
| **1. Walkthrough** | 3 นาที | เล่าส่วนที่ทำ (ไม่ใช้ slide เปิดโค้ดได้) | **B1**: เข้าใจโค้ดของตัวเอง | **25** (รวมช่วง 1–2) |
| **2. Code reading** | 5 นาที | ผู้สอนเลือกไฟล์/ฟังก์ชัน ให้อธิบายทีละบรรทัด + Input/Output | **B1** + **B2**: เหตุผลการออกแบบ & Trade-off | **20** (B2) |
| **3. What-if** | 5 นาที | เปลี่ยนเงื่อนไข/จำลอง error ทำนายผลและชี้จุดแก้ | **B3**: วิเคราะห์ปัญหาและการแก้ | **20** |
| **4. Results** | 3 นาที | อธิบายตัวเลขผลทดสอบ ข้อจำกัด และสิ่งที่ควรทดสอบเพิ่ม | **B4**: ตีความผลการทดลอง | **15** |
| **5. เชื่อมกับวิชา** | 3 นาที | คำถามเชิงแนวคิด Agentic AI ผูกกับโปรเจกต์ | **B5**: เชื่อมกับแนวคิดของวิชา | **20** |

> **กฎ Ownership ที่ต้องระวัง:** ถ้า B1 ได้ระดับ 1 (อธิบายโค้ดหลักไม่ได้) คะแนนส่วน A อาจถูกปรับไม่เกิน 50% — ดังนั้นต้องรู้ **Input → Process → Output** และ **ชี้บรรทัดในไฟล์จริง** ได้ทันทีโดยไม่ต้องเดา

---

## 1. ช่วงที่ 1: Walkthrough Script (3 นาที) — [B1]

> **โครงสร้างการเล่า (เปิดไฟล์จริงตามลำดับ 3 จุด):**
> 1. **ภาพรวมระบบ (30 วิ):** Finora เป็นเว็บจัดการการเงินส่วนบุคคล (Next.js 15 App Router + React 19 + TypeScript) ที่มี 2 ความสามารถ AI หลัก คือ **(1) Agentic AI Financial Coach & Action Assistant** (`POST /api/ai/chat`) และ **(2) Hybrid Thai Slip Scanner** (`POST /api/ai/slip`) ทำงานร่วมกับ State กลาง (`FinoraContext` + `localStorage`/`sessionStorage`) และ Virtual Bank Simulator (`POST /api/virtual-bank`)
> 2. **เส้นทางที่ 1 — AI Chat Agent Loop (1.5 นาที):** เปิด `src/app/(app)/ai-assistant/page.tsx` → `src/app/api/ai/chat/route.ts` → `src/lib/ai-tools.ts`
>    - ผู้ใช้พิมพ์คำถาม → Client สร้าง `financialSummary` (ยอดล่าสุด + Compact Insights) ส่งไป `/api/ai/chat` พร้อมประวัติแชท 8 ข้อความล่าสุด
>    - Server แปะ System Prompt (`buildFinoraSystemPrompt` v2.10.0) + Tool Definitions 10 ตัว (`FINORA_TOOLS`: 6 Read + 4 Write) แล้วยิง OpenRouter (`callOpenRouter`)
>    - Client รับ `toolCalls` ใน `runToolLoop` (จำกัดสูงสุด **4 รอบ**):
>      - ถ้าเป็น **Read Tool (🟢)** → รัน `runReadTool()` ทันทีบน Client แล้วส่งผล (`role: "tool"`) กลับให้ LLM สรุปคำตอบ
>      - ถ้าเป็น **Write Tool (🔴)** → ผ่าน `validateAndNormalizeWriteTool()` ก่อน แล้ว **ไม่ลงข้อมูลทันที** แต่รวบขึ้น **Blocking Confirmation Modal / Inline Card** ให้มนุษย์ตรวจสอบและกดยืนยัน (**Human-in-the-Loop**)
> 3. **เส้นทางที่ 2 — Hybrid Slip Scanner Workflow (1 นาที):** เปิด `src/lib/slip-image.ts` → `src/app/api/ai/slip/route.ts` → `src/lib/ocr.ts` → `src/lib/prompts.ts`
>    - Client ย่อรูปให้ไม่เกิน `950 KB / 1600px` (`prepareSlipImage`) เพื่อไม่ให้เกินลิมิต 1 MB ของ OCR.space
>    - Server ยิง **OCR.space** (`extractSlipOcrText`, timeout 4 วิ, best-effort) ได้ข้อความภาษาไทยมาช่วยยืนยันตัวสะกด → ส่ง **รูป (`detail: "high"`) + ข้อความ OCR + รายชื่อหมวดหมู่ + ชื่อผู้ใช้** ให้ Vision LLM (`jsonMode: true`, `temperature: 0`)
>    - แปลงและตรวจผลด้วย `parseSlipResult()` (จับคู่ชื่อผู้โอน/ผู้รับเพื่อแยก `income`/`expense` อัตโนมัติ) → เปิด Draft Modal ให้ผู้ใช้แก้/ยืนยันก่อนบันทึกจริง

---

## 2. ช่วงที่ 2: Code Reading ทีละบรรทัด (5 นาที) — [B1 + B2]

ด้านล่างคือ **6 ไฟล์หลัก** ที่ผู้สอนมีโอกาสเลือกให้ไล่โค้ดทีละบรรทัดมากที่สุด พร้อม Input/Output และจุดสำคัญ

### 2.1 `src/app/api/ai/chat/route.ts` (44 บรรทัด) — Chat API Endpoint
- **Input (`POST` JSON):** `{ prompt, messages, apiKeyOverride, financialSummary }`
- **การทำงานทีละขั้น:**
  - บรรทัด `10–18`: รองรับทั้ง `messages` (array ประวัติแชท) และ `prompt` (string เดี่ยว) ถ้าว่างคืน `400`
  - บรรทัด `20–30`: เรียก `callOpenRouter()` โดยส่ง `tools: FINORA_TOOLS` (10 tools) และแปะ `system` message จาก `buildFinoraSystemPrompt(financialSummary)` ไว้หน้าสุดของ `conversation`
  - บรรทัด `32–34`: ถ้า OpenRouter พัง ส่งต่อ `result.status` และ `result.error` กลับไปตรง ๆ
  - บรรทัด `36–39`: คืน `{ reply: result.text || "", toolCalls: result.toolCalls }` (ไม่ได้รัน tool ที่ฝั่ง server เพราะ state อยู่ใน `FinoraContext` ฝั่ง client)

### 2.2 `src/lib/openrouter.ts` (202 บรรทัด) — LLM Gateway Client
- **Input (`callOpenRouter`):** `{ messages, apiKeyOverride?, jsonMode?, temperature?, maxTokens?, tools? }`
- **Output:** `{ ok, status, text?, toolCalls?, metrics?, error? }`
- **จุดที่ต้องอธิบายได้เป๊ะ:**
  - **Config & Key Precedence** (บรรทัด `64–84`): ใช้ `apiKeyOverride` (จากหน้า Settings) ก่อน ถ้าไม่มีค่อย fallback ไป `process.env.OPENROUTER_API_KEY` และต้องมี `OPENROUTER_MODEL`
  - **Request Body** (บรรทัด `86–95`):
    - ถ้า `jsonMode: true` → แนบ `response_format: { type: "json_object" }` (ใช้ใน Slip Scanner)
    - ถ้ามี `tools` → แนบ `tools` และ `tool_choice: "auto"` (ใช้ใน AI Chat)
  - **Timeout & Retry Strategy** (บรรทัด `41–43`, `98–199`):
    - `OPENROUTER_TIMEOUT_MS = 45_000` (45 วินาที ผ่าน `AbortSignal.timeout`)
    - Retry สูงสุด **2 ครั้ง** (`attempt = 0, 1, 2` รวมยิงได้ 3 ครั้ง) หน่วง `[1000ms, 2000ms]` เฉพาะ HTTP `429, 502, 503, 504` หรือ transient network error
    - **ไม่ retry** ถ้าเกิด `TimeoutError` / `AbortError` (คืน `504` ทันทีที่บรรทัด `181–187`) เพื่อไม่ให้ user รอนานเกิน 45 วิ × 3
  - **Defensive JSON & Empty Reply Handling** (บรรทัด `112–168`):
    - อ่าน `res.text()` แล้วค่อย `JSON.parse` ใน `try/catch` (ถ้า response ไม่ใช่ JSON คืน `502` ที่บรรทัด `140–142`)
    - เช็ก `!toolCalls && (!content || !content.trim())` (บรรทัด `154–168`): ถ้า `finishReason === "length"` จะบอกชัดเจนว่าโมเดลตัดจบเพราะเกินความยาว (เช่น reasoning model ใช้ token หมดก่อนตอบ)
  - **Observability** (บรรทัด `170–177`): log `LLMMetrics` (`model`, `inputTokens`, `outputTokens`, `totalTokens`, `latencyMs`)

### 2.3 `src/app/(app)/ai-assistant/page.tsx` — Agent Loop (`runToolLoop`, บรรทัด `667–946`)
นี่คือ **หัวใจของ Agentic Loop** ในโปรเจกต์นี้:
- **Context Windowing** (`sendPrompt`, บรรทัด `958–962`): ตัดประวัติแชทส่งเฉพาะ **8 ข้อความล่าสุด** (`.slice(-8)`) + ข้อความใหม่ เพื่อคุม token และลด noise
- **Loop Termination Guard** (บรรทัด `681`): `for (let step = 0; step < 4; step++)` → **จำกัดสูงสุด 4 รอบต่อ 1 เทิร์น** ถ้ารันครบ 4 รอบแล้วยังไม่จบ จะหลุดลูปไปแจ้งข้อความ fallback ที่บรรทัด `941–945`
- **Fake Tool-Confirmation Recovery** (บรรทัด `702–719`):
  - ปัญหาที่เจอบ่อยใน LLM: โมเดลพิมพ์ข้อความหลอกว่า *"เตรียมรายการให้แล้ว กดยืนยันในการ์ดได้เลย"* แต่ **ไม่ส่ง `tool_calls` มาจริง**
  - โค้ดใช้ Regex ตรวจข้อความกลุ่มนี้ แล้ว inject ข้อความ `[ระบบ] คุณยังไม่ได้ส่ง Function Call...` กลับเข้า `history` เพื่อบังคับให้ LLM ลองใหม่ (**ทำได้แค่ 1 ครั้ง** ด้วย flag `retriedMissingTool` กันวนลูป)
- **Tool Argument JSON Parsing** (บรรทัด `731–749`):
  - `JSON.parse(tc.function.arguments)` อยู่ใน `try/catch` + ตรวจว่าต้องเป็น Object ไม่ใช่ Array
  - ถ้า parse พัง (`parseError = true`) → ไม่ crash แต่ส่ง `role: "tool"` กลับไปบอก LLM ว่า *"รูปแบบ JSON ของ arguments ไม่ถูกต้อง กรุณาเรียกเครื่องมือใหม่อีกครั้ง"* (บรรทัด `768–778`) ให้ LLM self-correct ในรอบถัดไป!
- **Dependency-Aware Priority Sorting** (บรรทัด `750–754`, อ้าง `getToolExecutionPriority` ใน `ai-tools.ts:64–74`):
  - เรียงลำดับการประมวลผลเมื่อ LLM ส่งหลาย Tool พร้อมกันในรอบเดียว:
    - `0`: Read Tools (`get_*`)
    - `1`: `add_category` (สร้างหมวดใหม่ก่อน)
    - `2`: `add_transaction` ที่เป็น `income` (รายรับเข้าก่อน)
    - `3`: `update_category_budget`, `add_recurring_payment`
    - `4`: `add_transaction` ที่เป็น `expense`
- **Chained Category + Transaction & Batching** (บรรทัด `756–871`, `899–938`):
  - สร้าง `workingCategories` จำลองหมวดใหม่ด้วย `draft-cat-0` ไว้ก่อน เพื่อให้ `add_transaction` ที่ตามมาในรอบเดียวกันสามารถ `resolveCategory` หาชื่อหมวดใหม่เจอได้ทันที
  - รวบรายการธุรกรรมทั้งหมดเข้า `pendingTxDraft` (เปิด Modal เดียว) หรือ `pendingBudgetDraft` หรือ `pendingTool` card แล้ว **`return` ออกจาก `runToolLoop` ทันที** เพื่อรอผู้ใช้กดยืนยัน (Human-in-the-Loop)
  - ตอนผู้ใช้กดยืนยัน (`handleConfirmTool` บรรทัด `985` / `handleConfirmTxDraft` บรรทัด `1189`): จะรัน `validateAndNormalizeWriteTool` **ซ้ำอีกรอบ** (กัน state เปลี่ยนระหว่างรอ) แล้วค่อยอัปเดต `FinoraContext` โดย **ไม่ยิง LLM ซ้ำ**

### 2.4 `src/lib/ai-tools.ts` (1,088 บรรทัด) — Tool Registry, Read Engine & Write Validator
- **10 Tools แบ่งเป็น 2 กลุ่มชัดเจน:**
  - **6 Read Tools (🟢 ปลอดภัย/ไม่มี side-effect)** (`READ_TOOL_NAMES`, บรรทัด `24–31`):
    1. `get_financial_overview` — ยอด Wallet, รายรับ/รายจ่ายรวม และเฉพาะรอบโควต้าปัจจุบัน
    2. `get_transactions` — กรองตาม `type`, `categoryId`, `startDate`, `endDate`, `currentCycleOnly`, `limit` (cap 1–30) + สลับ `startYmd`/`endYmd` ให้อัตโนมัติถ้าส่งกลับด้าน
    3. `get_categories` — งบ/ใช้จริง/คงเหลือ/เกินงบ/% ในรอบโควต้าปัจจุบัน (กรองหมวดสงวน `"recurring"` ออก)
    4. `get_recurring_payments` — สรุปรายจ่ายประจำ vs รายรับประจำ และ net recurring รายเดือน
    5. `get_financial_prediction` — คาดการณ์ล่วงหน้า 1–12 เดือน โดยใช้ `Math.max(recentTx, activeRecurring)` เพื่อ **ไม่นับซ้ำ (avoid double-counting)** ระหว่างบิลประจำกับธุรกรรมจริง + คืน `assumptions[]` และ `lowBalanceRisk`
    6. `get_spending_insights` — เรียก `computeFinancialInsights()` รองรับพารามิเตอร์ `sections[]` เพื่อดึงเฉพาะส่วนที่ต้องใช้ ลด token bloating
  - **4 Write Tools (🔴 มี side-effect — ต้องผ่าน Validation + User Confirmation เสมอ)** (`WRITE_TOOL_NAMES`, บรรทัด `56–61`):
    1. `add_transaction`
    2. `update_category_budget`
    3. `add_category`
    4. `add_recurring_payment`
- **`resolveCategory()`** (บรรทัด `191–230`): ลำดับการจับคู่ 5 ขั้น:
  1. Exact ID → 2. Exact Name (ให้หมวด Custom เช่น `"กาแฟ"` ชนะ alias พื้นฐาน!) → 3. Exact Alias (`CATEGORY_ALIASES` ซึ่งวาง `รถไฟฟ้า -> transport` ไว้ก่อน `ไฟฟ้า -> housing` เพื่อไม่ให้ชนคำย่อย) → 4. Substring Name → 5. Substring Alias (พร้อมใช้ `title` เป็น `fallbackText` หาก LLM ส่ง `categoryId: "other"`)
- **`validateAndNormalizeWriteTool()`** (บรรทัด `851–1088`):
  - ปัดทศนิยม 2 ตำแหน่ง (`roundCurrency`), normalize ช่องทาง (`cash -> เงินสด`, `promptpay -> สแกน PromptPay QR`), แปลงวันที่ พ.ศ./ค.ศ./คำสัมพัทธ์ (`เมื่อวาน`, `15/08`) ผ่าน `normalizeTransactionDate`
  - **Guardrails ใน Validator:** ห้ามแก้/สร้างหมวดสงวน `"recurring"`, ห้ามสร้างหมวดซ้ำ (แนะนำให้ใช้ `update_category_budget` แทน), ห้ามสร้าง recurring ซ้ำชื่อ, ตรวจ `amount > 0`, `budget >= 0`, แนบคำเตือน `⚠️` แบบ non-blocking ถ้างบใหม่ต่ำกว่ายอดที่ใช้ไปแล้ว หรือวันครบกำหนดอยู่ในอดีต

### 2.5 Hybrid Slip Scanner: `slip-image.ts` → `api/ai/slip/route.ts` → `ocr.ts` → `prompts.ts`
- **Step 1 — Client Image Prep (`src/lib/slip-image.ts:7–74`):**
  - ถ้ารูป `<= 950 KB` และด้านยาว `<= 1600px` และเป็น PNG/JPEG/WEBP → ส่งตรง (`canSendDirect`)
  - ถ้าใหญ่กว่า → วาดลง Canvas พื้นหลังสีขาว (กัน PNG โปร่งใสอ่านไม่ออก) ย่อไม่เกิน `1600px` แล้วลด JPEG quality ไล่ระดับ `[0.88, 0.78, 0.68]` จน `base64.length <= 1_280_000` (ใต้ลิมิต 1 MB ของ OCR.space) + สร้าง thumbnail `180px` เพื่อไม่ให้ `sessionStorage` เต็ม
- **Step 2 — Server OCR (`src/lib/ocr.ts:16–84`):**
  - ส่งไป `https://api.ocr.space/parse/image` (`language: "tha"`, `OCREngine: "1"`, timeout **4 วินาที**, max base64 `1_350_000`)
  - **Best-effort / Graceful Degradation:** ถ้าเกิน 1 MB, timeout, HTTP error, หรือ `IsErroredOnProcessing` → log warning แล้วคืน `undefined` **ไม่ throw error** เพื่อให้ Vision LLM ทำงานต่อด้วยรูปภาพอย่างเดียวได้
  - `cleanOcrText()` ลบข้อความ `[Note: ...]` ของ demo key (`helloworld`) และตัดความยาวไม่เกิน 2,500 ตัวอักษร
- **Step 3 — Vision LLM (`src/app/api/ai/slip/route.ts:28–69`):**
  - สร้าง prompt ด้วย `buildSlipPrompt(categoryList, userName, ocrText)`
  - เรียก `callOpenRouter` ด้วย `jsonMode: true`, `temperature: 0`, `detail: "high"`
- **Step 4 — Post-processing & Defensive Parser (`src/lib/prompts.ts:142–210`):**
  - ลอก Markdown fence ` ```json ... ``` ` ออกก่อน `JSON.parse` (กัน LLM แถม backtick แม้เปิด `jsonMode`)
  - `cleanSlipName()` ตัด `...` และอักษรย่อท้ายชื่อที่แอปธนาคารตัดคำ (เช่น `"นาย ชัยณรงค์ แ..."` → `"นาย ชัยณรงค์"`)
  - `namesLikelyMatch()` ตัดคำนำหน้า (`คุณ/นาย/นางสาว/Mr.`) แล้วเทียบกับ `userName`: ถ้าผู้รับตรงกับผู้ใช้ (และผู้โอนไม่ใช่) → บังคับ `type = "income"`, `merchant = sender`; นอกนั้นเป็น `expense`, `merchant = receiver` (Deterministic override แก้ปัญหา LLM สับสนทิศทางเงินเข้า/ออก)
  - Sanitize `amount` (ลบ `,` และ `"บาท"`), กันหมวด `"recurring"` (fallback เป็น `salary`/`food`), แปลงวันที่ พ.ศ. → ค.ศ. (`YYYY-MM-DD`)

### 2.6 Deterministic Analytics & State Safety (`financial-insights.ts` & `FinoraContext.tsx`)
- **`src/lib/financial-insights.ts`:** คำนวณตัวเลขทั้งหมดด้วย TypeScript ล้วน (MoM %, Savings Rate %, Burn Pace status: `over >= 100%`, `near >= 80%`, `ahead_of_pace > elapsedPct + 15%`, Daily Safe Spend, Savings Opportunities พร้อม `sampleTransactions` จริงและ `SAVINGS_DISCLAIMER`) → ส่งเป็น Compact Summary เข้า System Prompt และคืน JSON เต็มผ่าน `get_spending_insights`
- **`src/context/FinoraContext.tsx` (Gotcha สำคัญ):** ใช้ `bankBalanceRef.current`, `txSeqRef`, `catSeqRef` อัปเดตยอดแบบ synchronous ในลูปเวลาบันทึกหลายรายการพร้อมกัน (Batch Modal) เพื่อป้องกัน **React Stale Closure** ที่จะทำให้ยอดเงินถูกเขียนทับหรือเกิด duplicate key

---

## 3. เหตุผลการออกแบบ & Trade-offs — [B2 (20 คะแนน)]

| การตัดสินใจที่เลือก | เหตุผลที่เลือก | ทางเลือกอื่น | ข้อดี / ข้อเสียของทางเลือกอื่น |
| :--- | :--- | :--- | :--- |
| **OpenRouter Gateway** (`src/lib/openrouter.ts`) แทน SDK ค่ายเดียว | เปลี่ยนโมเดลได้ทันทีผ่าน `.env` (`OPENROUTER_MODEL`) โดยไม่ต้องแก้โค้ด รองรับทั้ง Chat Tools และ Vision ใน OpenAI-compatible schema เดียว | ใช้ Google Gemini SDK หรือ OpenAI SDK ตรง | **ดี:** Latency ต่ำกว่า 1 hop, ใช้ฟีเจอร์เฉพาะค่ายได้เต็ม (เช่น native structured output schema) / **แย่:** Vendor lock-in สลับเปรียบเทียบโมเดลยาก |
| **Client-side Tool Execution + Blocking Modal** | State การเงินอยู่ใน React Context + `localStorage` ทำให้ Read Tool รันได้ทันที (0 ms network) และ Write Tool มี **Human-in-the-Loop** ให้ผู้ใช้แก้ฟิลด์/วันที่ก่อนบันทึก | Server-side Tool Loop + DB + Auto-Execute | **ดี:** ปลอดภัยกว่าในแง่ multi-user/audit log, ลด round-trip ของ Read Tool / **แย่:** ถ้า Auto-Execute ไม่มี confirmation เสี่ยง LLM บันทึกยอด/หมวดผิดลงบัญชีจริง |
| **Hybrid OCR.space + Vision LLM** (`api/ai/slip`) | Vision LLM เก่งเรื่อง layout/ทิศทางโอน แต่บางทีสะกดภาษาไทยเพี้ยนหรืออ่านลายน้ำปน; OCR.space ช่วย anchor ตัวสะกดไทยและตัวเลข โดยมี timeout 4 วิแบบ graceful fallback | 1) Pure Vision LLM<br>2) Pure OCR + Regex | 1) **Pure Vision:** เร็วกว่า ไม่ต้องพึ่ง external OCR แต่ตัวสะกดไทยแม่นน้อยกว่าในสลิปความละเอียดต่ำ<br>2) **Pure OCR + Regex:** ถูก/เร็ว/deterministic แต่พังง่ายเมื่อธนาคารเปลี่ยน layout สลิปหรือเจอ e-Wallet รูปแบบใหม่ |
| **Deterministic Analytics Engine (`financial-insights.ts`)** | ให้โค้ดคำนวณ MoM, Burn Rate, Daily Safe Spend แทน LLM เพื่อให้ตัวเลข **แม่น 100% ไม่ hallucinate** และทดสอบด้วย unit test ได้ | ส่ง Raw Transactions ทั้งหมดให้ LLM คำนวณเอง | **ดี:** เขียนโค้ดน้อยกว่า ยืดหยุ่นต่อคำถามแปลก ๆ / **แย่:** เปลือง token มหาศาล, LLM คิดเลข/รวมยอดผิดบ่อย, ควบคุม logic ไม่ได้ |
| **3-Tier Coaching Prompt (`v2.10.0`)** + **`sections` filter** | แยกความยาวคำตอบตามเจตนา (ทักทาย/บันทึก → สั้น, สรุปยอด → มีคำแนะนำ 2–3 ข้อ, วิเคราะห์ → เต็ม 4 ขั้น) + กรองข้อมูลที่ tool ส่งกลับเพื่อลด token | Prompt สั้น ๆ ให้ LLM ตอบอิสระ | **ดี:** System prompt สั้น ประหยัด input token / **แย่:** คำตอบแกว่ง บ้างก็ยาวเกินตอนแค่สั่งจดกาแฟ บ้างก็แนะนำกว้างลอย ๆ ไม่ชี้รายการจริง |
| **Virtual Wallet ติดลบได้ (Negative Balance Policy)** | Finora เป็นตัวบันทึกรายรับ-รายจ่ายจริง (Tracker) ไม่ใช่ระบบโอนเงินธนาคารจริง ผู้ใช้ต้องบันทึกรายจ่ายหรือตัดบิลประจำได้แม้ยังไม่ได้กรอกเงินเดือนเข้า | บล็อกเมื่อ `amount > bankBalance` | **ดี:** สมจริงแบบบัญชีธนาคาร / **แย่:** ขัดขวาง UX การจดรายจ่าย (เช่น จ่ายด้วยเงินสด/บัตรเครดิต หรือลืมจดเงินเดือนก่อนจดค่าข้าว) |
| **Client-side Storage (`localStorage`/`sessionStorage`)** | ใช้งานได้ทันที ไม่ต้องตั้ง DB/Auth server เหมาะกับ prototype/personal sandbox | PostgreSQL + Auth + Server API | **ดี:** Sync ข้ามอุปกรณ์ได้ ปลอดภัยกว่าเก็บบน browser / **แย่:** ซับซ้อนกว่าและเพิ่ม infra overhead ในขอบเขตโปรเจกต์ |

---

## 4. ช่วงที่ 3: What-if Scenarios & จุดแก้โค้ด (5 นาที) — [B3 (20 คะแนน)]

> **เทคนิคการตอบช่วงนี้:** ตอบ 3 ส่วนเสมอ — **(1) ทำนายผลที่จะเกิดตามโค้ดปัจจุบัน** → **(2) อ้างชื่อไฟล์/ฟังก์ชันจริง** → **(3) ชี้จุดที่ต้องแก้ถ้าอยากเปลี่ยนพฤติกรรม**

### Q1: ถ้า Model วนเรียก Tool เดิมซ้ำ 50 รอบ ระบบหยุดตรงไหน?
- **สิ่งที่จะเกิด:** ระบบ **ไม่วนถึง 50 รอบ** แต่จะหยุดที่ **4 รอบ** เพราะ `runToolLoop` ใน `src/app/(app)/ai-assistant/page.tsx:681` ใช้ `for (let step = 0; step < 4; step++)` เมื่อครบ 4 รอบแล้วยังไม่ได้คำตอบสุดท้าย จะหลุดลูปไปแสดงข้อความ *"ฟิโนร่าประมวลผลหลายขั้นตอนแล้วยังไม่ได้ข้อสรุปสุดท้ายค่ะ..."* (บรรทัด `941–945`)
- **ข้อสังเกตเชิงลึก (จุดอ่อนที่ชี้เพิ่มได้):** ตอนนี้ยังไม่มี **duplicate tool-call detection** (เช่น ถ้าโมเดลเรียก `get_categories({})` ซ้ำกัน 4 รอบติด ก็จะเสียโควต้าไปทั้ง 4 รอบ)
- **จุดแก้:** เพิ่ม `seenCalls = new Set<string>()` เก็บ `${tc.function.name}:${tc.function.arguments}` ใน `runToolLoop` (`ai-assistant/page.tsx:679`) ถ้าซ้ำให้ส่ง tool error หรือหยุดลูปทันที

### Q2: ถ้า Tool คืนค่า Error หรือ LLM ส่ง JSON arguments ผิดรูป เกิดอะไร?
- **JSON ผิดรูป (`tc.function.arguments` พัง หรือเป็น Array):**
  - `ai-assistant/page.tsx:737–747` จับ `catch` ตั้ง `parseError = true` → บรรทัด `768–778` ส่ง `{ role: "tool", content: '{"error":"รูปแบบ JSON ของ arguments ไม่ถูกต้อง..."}' }` กลับให้ LLM แก้ตัวในรอบถัดไป (ภายในงบ 4 รอบ)
- **Write Tool Validation ไม่ผ่าน (เช่น `amount <= 0`, หมวดไม่รู้จัก, วันที่ `31/02`):**
  - `validateAndNormalizeWriteTool` คืน `{ ok: false, error }` → บรรทัด `785–790` ส่ง error กลับเป็น `role: "tool"` ให้ LLM แจ้งผู้ใช้ตามจริงหรือเรียกใหม่ด้วยค่าที่ถูกต้อง
- **LLM เรียกชื่อ Tool ที่ไม่มีในระบบ (Hallucinated Tool Name):**
  - ตก `else` ที่บรรทัด `872–880` ส่ง `{ error: 'ไม่พบเครื่องมือชื่อ "..." ในระบบ' }` กลับให้ LLM
- **จุดแก้หากต้องการให้ fail-fast แทนการให้ LLM ลองใหม่:** เปลี่ยนจาก `history.push({ role: "tool", ... })` เป็น `appendChatMessage({ role: "error", ... }); return;`

### Q3: ถ้า `parseSlipResult` เจอ JSON ผิดรูปจาก Vision LLM เกิดอะไร?
- **สิ่งที่จะเกิด:** `parseSlipResult` (`src/lib/prompts.ts:142–210`) พยายามลอก ` ```json ` ออกก่อนแล้ว `JSON.parse` ถ้ายังพังจะตก `catch` คืน `{ ok: false, error: "ไม่สามารถแปลงผลลัพธ์ JSON จาก AI ได้" }`
- `src/app/api/ai/slip/route.ts:61–63` จะคืน **HTTP 502** พร้อม error message
- ฝั่ง UI (`ai-assistant/page.tsx:1124–1140`) จะแสดงข้อความ error ในแชทและไม่เปิด Draft Modal
- **จุดแก้ถ้าอยากให้ทนทานขึ้น:** เพิ่ม 1 retry ใน `/api/ai/slip/route.ts` เมื่อ `!validated.ok` หรือใช้ regex ดึง `{ ... }` ก้อนแรกออกจาก `rawText` ก่อน `JSON.parse`

### Q4: ถ้า OCR.space ล่ม, ช้าเกิน 4 วินาที, หรือรูปใหญ่เกิน 1 MB เกิดอะไร?
- **สิ่งที่จะเกิด:** ระบบ **ไม่พัง** เพราะ `extractSlipOcrText` (`src/lib/ocr.ts:16–84`) ออกแบบเป็น **Best-effort**:
  - ถ้ารูปเกิน `1_350_000` chars (base64), HTTP ไม่ ok, `IsErroredOnProcessing`, หรือเกิน `OCR_SPACE_TIMEOUT_MS = 4000` → log `console.warn("[OCR] skipped", ...)` แล้วคืน `undefined`
  - `buildSlipPrompt` (`src/lib/prompts.ts:85–87`) จะไม่แปะบล็อก OCR → Vision LLM จะวิเคราะห์จากรูปภาพโดยตรง (`detail: "high"`) แทน
- **จุดแก้ถ้าอยากรู้ใน UI ว่าใช้ OCR หรือไม่:** คืน flag `ocrUsed: Boolean(ocrText)` จาก `/api/ai/slip/route.ts` กลับไปแสดง badge/telemetry

### Q5: ถ้า OpenRouter คืน `429` (Rate Limit), `503`, หรือ Timeout 45 วินาที เกิดอะไร?
- **`429 / 502 / 503 / 504`:** `callOpenRouter` (`src/lib/openrouter.ts:130–135`) จะ retry อัตโนมัติสูงสุด 2 ครั้ง หน่วง `1s` แล้ว `2s` ถ้ายิงครบแล้วยังพัง คืน `{ ok: false, status, error }` ไปแสดงในแชท
- **Timeout 45 วินาที:** จับ `TimeoutError`/`AbortError` ที่บรรทัด `181–187` → คืน `504` **ทันทีโดยไม่ retry**
- **`finish_reason === "length"` และ `content` ว่าง:** คืน `502` พร้อมข้อความแจ้งว่า AI ประมวลผลเกินขีดจำกัดความยาว (บรรทัด `160–167`)
- **จุดแก้:** ถ้าอยากปรับ backoff หรือเพิ่ม `maxTokens` ก็แก้ที่ `RETRY_DELAYS_MS` (`openrouter.ts:43`) หรือส่ง `maxTokens` ใน `api/ai/chat/route.ts:20`

### Q6: ถ้าเปลี่ยน Provider / Model (เช่น `google/gemini-2.5-flash` → `openai/gpt-4o-mini` หรือ model เล็ก) ผลจะเปลี่ยนไหม ทำไม?
- **เปลี่ยนแน่นอน** แม้ใช้ OpenRouter schema เดียวกัน เพราะ:
  1. **Tool-Calling Reliability:** โมเดลบางตัวชอบ "พูดแทนเรียก tool" (จึงต้องมี `looksLikeFakeToolConfirm` recovery ใน `ai-assistant/page.tsx:704–719`) หรือส่ง `categoryId` เป็นภาษาไทย/คำพ้องแทน ID (จึงต้องมี `resolveCategory` + `CATEGORY_ALIASES`)
  2. **JSON Mode Compliance:** บางโมเดลบน OpenRouter ไม่รองรับ `response_format: { type: "json_object" }` หรือยังห่อด้วย ` ```json ` (จึงต้องมี `parseSlipResult` ลอก fence)
  3. **Thai Vision & OCR Robustness:** ความแม่นยำในการอ่านสระ/วรรณยุกต์ไทยและตัวเลขบนสลิปต่างกันมากระหว่าง Vision model (นี่คือเหตุผลที่มี OCR.space เสริม + cell เปรียบเทียบโมเดลใน `qr_scan_eval.ipynb`)
  4. **Instruction Following (3-Tier Coaching & Persona):** โมเดลเล็กอาจหลุดคำลงท้าย `ค่ะ/นะคะ`, หลุด Off-Topic Guardrail, หรือไม่ยอมใช้ `sections` filter
- **จุดแก้เมื่อสลับโมเดล:** เปลี่ยนแค่ `OPENROUTER_MODEL` ใน `.env.local` แล้วรัน `eval-cases.json` + `qr_scan_eval.ipynb` เทียบ accuracy/latency/cost

### Q7: ถ้า User สั่ง "เพิ่มหมวดสัตว์เลี้ยง งบ 3,000 แล้วบันทึกค่าอาหารแมว 500 ลงหมวดนี้" เกิดอะไร?
- **สิ่งที่จะเกิด:** LLM ส่ง 2 `toolCalls` (`add_category` + `add_transaction`) ในรอบเดียวกัน
- `getToolExecutionPriority` เรียงให้ `add_category` (priority 1) มาก่อน `add_transaction(expense)` (priority 4)
- `runToolLoop` (`ai-assistant/page.tsx:791–818`) สร้างหมวดชั่วคราว `draft-cat-0` ใส่ `workingCategories` → `add_transaction` ที่ตามมาจับคู่ `categoryId: "สัตว์เลี้ยง"` เข้ากับ `draft-cat-0` ได้สำเร็จ แล้วรวบเปิด **Chained Confirmation Modal** เดียว
- พอกดยืนยัน (`handleConfirmTxDraft`) จะสร้างหมวดจริงก่อน ได้ real ID แล้ว map แทน `draft-cat-0` ก่อนบันทึกรายการ

### Q8: ถ้า User พยายาม Prompt Injection หรือสั่งลบหมวด/พักบิลในแชท เกิดอะไร?
- **Prompt Injection / นอกเรื่อง:** System Prompt มี `# Scope & Off-Topic Guardrail` (`prompts.ts:68–73`) สั่งปฏิเสธ 1–2 ประโยคโดยไม่ตอบเนื้อหานอกเรื่อง
- **สั่งลบหมวด / พักบิลประจำ:** แม้ LLM จะถูกหลอกให้พยายามเรียก `delete_category` หรือ `toggle_recurring` ก็ **ทำไม่ได้ในระดับโค้ด** เพราะ **ไม่มี Tool เหล่านั้นอยู่ใน `FINORA_TOOLS` และ `WRITE_TOOL_NAMES`** (Hard Boundary / Least Privilege) และ `validateAndNormalizeWriteTool` จะปฏิเสธคำสั่งที่ไม่รองรับ (ยืนยันด้วย Test #23 ใน `self-check.ts:573–574`)

---

## 5. ช่วงที่ 4: Results & Evaluation (3 นาที) — [B4 (15 คะแนน)]

### 5.1 หลักฐานการทดสอบที่มีจริงในโปรเจกต์

| ชุดทดสอบ | ไฟล์ | ประเภท | ผลลัพธ์จริง / สิ่งที่วัด |
| :--- | :--- | :--- | :--- |
| **1. Automated Self-Check** | `src/lib/self-check.ts` (`npm run test:check`) | Deterministic Unit & Integration Tests | **ผ่าน 30/30 checks** (รันยืนยันได้ทันทีในห้องสอบ) ครอบคลุม: Virtual Bank (credit/debit/overdraft/invalid), AI Read Tools, Write Validator & Aliases, Priority Ordering, `parseSlipResult`, Rollover Simulator, Date Normalization (พ.ศ./ค.ศ./คำไทย), Financial Insights (MoM/Burn Pace/Edge cases), Prompt v2.10.0 |
| **2. LLM Behavioral Eval Set** | `tests/llm/eval-cases.json` | Curated Test Suite (20 cases) | นิยาม **20 เคส** ครอบคลุม: Read Tools (3), Write Tools (6), Multi-tool Ordering (1), Off-topic & Prompt Injection Guardrail (2), Persona (1), Coaching & Data Honesty & Proactivity (7) |
| **3. Slip Vision Diagnostic Notebook** | `tests/llm/qr_scan_eval.ipynb` | Interactive Eval & Model Comparison | แยกวินิจฉัยทีละ Stage (Image Prep → OCR → Vision LLM → Post-processing) + เปรียบเทียบหลายโมเดล (`gemini-2.5-flash` vs `gpt-4o-mini`) + วัด `merchant_exact`, `merchant_partial`, `amount_ok`, `type_ok`, `category_ok`, `latency_ms`, `total_tokens` |

### 5.2 ข้อจำกัดที่ต้องบอกอย่างตรงไปตรงมา (เพื่อให้ได้ B4 ระดับ 4!)
1. **Deterministic vs Non-Deterministic:** `30/30` ของ `self-check.ts` พิสูจน์ความถูกต้องของ **โค้ดรอบนอก LLM (tools, parsers, math, validators)** ได้ 100% แต่ **ไม่ได้วัดพฤติกรรมของ LLM จริง** (stochastic)
2. **ขนาดชุดทดสอบ (Sample Size เล็ก):**
   - `eval-cases.json` มี 20 เคส (และยังไม่มี automated runner script ที่ยิง API จริงแล้วสรุปตารางคะแนนอัตโนมัติใน repo)
   - `qr_scan_eval.ipynb` มี `GROUND_TRUTH` ตัวอย่างตั้งต้นเพียง 1 รูป (`sample_kplus_slip.jpg`) — ตัวเลข 100% จาก 1 รูปไม่สามารถสรุป generalization ข้ามธนาคารได้
3. **ความแปรปรวนระหว่างรอบ (Run-to-Run Variance):**
   - Chat route ไม่ได้ล็อก `temperature: 0` (ใช้ default ของโมเดล) ทำให้คำตอบเชิง coaching และลำดับการเรียก tool อาจต่างกันได้ระหว่างรอบ (ต่างจาก Slip route ที่ตั้ง `temperature: 0` เพื่อความเสถียร)
4. **ความเสี่ยงเรื่อง Prompt Overfitting / Data Leakage:**
   - กฎบางข้อใน `buildSlipPrompt` (เช่น รูปแบบสลิป K+, TrueMoney, ชื่อร้านขึ้นบรรทัดใหม่, ตัด `...`) ถูกปรับจูนจากการเห็นสลิปตัวอย่างจริง (in-sample tuning) จึงต้องทดสอบกับ **held-out slips** จากธนาคารและร้านค้าที่ไม่เคยเห็นมาก่อน
5. **Code-Notebook Drift:**
   - `qr_scan_eval.ipynb` ยังมีโค้ดจำลอง QR Decoder / EMVCo TLV (จากเวอร์ชันก่อนหน้า v2.5.0) ขณะที่แอปจริงปัจจุบัน simplify เหลือ `slip-image.ts` + `OCR.space` + `Vision LLM` (`POST /api/ai/slip`, prompt v2.10.0) แล้ว — ต้องอัปเดต notebook ให้ sync กับ production pipeline ก่อนรัน benchmark รอบใหม่

### 5.3 สิ่งที่ควรทดสอบเพิ่มเติม (Next Evaluation Plan)
1. **Automated Eval Runner + Pass@k / Consistency (รันซ้ำ 3–5 รอบ/เคส):** ยิง `eval-cases.json` 20 เคสผ่าน `/api/ai/chat` จริง วัด Tool Selection Accuracy, Argument Exact Match, Guardrail Refusal Rate และ Latency/Token Cost
2. **Held-out Thai Slip Benchmark (30–50 รูป):** ครอบคลุมหลายธนาคาร (K+, SCB, KTB, BBL, Krungsri, TTB, TrueMoney), สลิปเงินเข้า vs เงินออก, รูปเบลอ/แสงน้อย/ครอปขอบ, และ **Negative cases** (รูปแมว/รูปอาหารที่ไม่ใช่สลิป เพื่อวัด False Positive Rate ของ `isValidPaymentImage`)
3. **Adversarial & Red-Team Suite:** ทดสอบ Prompt Injection ที่ซับซ้อนขึ้น (เช่น ซ่อนคำสั่งในชื่อร้านค้าบนสลิป — **Indirect Prompt Injection**)

---

## 6. ช่วงที่ 5: เชื่อมกับแนวคิดของวิชา Agentic AI (3 นาที) — [B5 (20 คะแนน)]

### 6.1 งานนี้ควรเป็น "Workflow" หรือ "Agent"? (คำถามยอดฮิต!)
Finora ใช้สถาปัตยกรรมแบบ **Hybrid (แยกใช้ตามลักษณะงาน — ไม่เอา Agent ไปยัดทุกที่)**:
- **Slip Scanner (`POST /api/ai/slip`) = Deterministic Workflow (Pipeline):**
  - ขั้นตอนตายตัวแน่นอนทุกครั้ง: `Resize Image → Best-Effort OCR → Vision LLM (JSON) → Deterministic Parser & User-Name Matcher → Draft Modal`
  - **ทำไมไม่ใช้ Agent:** ไม่จำเป็นต้องให้ LLM ตัดสินใจเองว่าจะเรียก OCR หรือจะ parse ยังไง ใช้ Workflow ที่กำหนดลำดับตายตัวจะได้ **latency ต่ำกว่า, cost ถูกกว่า, predictable กว่า และ debug ง่ายกว่า**
- **AI Financial Assistant (`runToolLoop` + `POST /api/ai/chat`) = Bounded Tool-Using Agent (ReAct-style Loop):**
  - คำถามผู้ใช้ปลายเปิดและคาดเดาไม่ได้ล่วงหน้า (อาจแค่ทักทาย, ถามยอด, ขอวิเคราะห์เชิงลึก, หรือสั่งสร้างหมวดพร้อมจดหลายรายการในประโยคเดียว)
  - LLM ทำหน้าที่เป็น **Orchestrator** ตัดสินใจเลือกเรียก Read/Write Tools เองได้หลายรอบ (bounded สูงสุด 4 steps) และแก้ตัวได้เมื่อ tool คืน error

### 6.2 การจัดระดับความเสี่ยงของ Tool (🟢 Read vs 🔴 Write) และ Guardrails
- **🟢 Low-Risk / Read-Only Tools (6 ตัว):** `get_financial_overview`, `get_transactions`, `get_categories`, `get_recurring_payments`, `get_financial_prediction`, `get_spending_insights`
  - ไม่มี side-effect → **Auto-execute** ได้ทันทีในลูป
- **🔴 High-Risk / State-Mutating Tools (4 ตัว):** `add_transaction`, `update_category_budget`, `add_category`, `add_recurring_payment`
  - มี side-effect เปลี่ยนข้อมูลการเงินและยอด Wallet → ใช้ **Defense-in-Depth Guardrails 5 ชั้น:**
    1. **Least Privilege (No Destructive Tools):** ไม่มี tool สำหรับ *ลบ* หมวดหมู่/ธุรกรรม หรือ *พัก* บิลประจำในแชท ต้องไปกดในหน้า UI เอง
    2. **Prompt Guardrail:** กฎ Off-Topic/Injection + ห้ามอ้างว่าบันทึกแล้วก่อนผู้ใช้กดยืนยัน
    3. **Schema & Deterministic Validator (`validateAndNormalizeWriteTool`):** ตรวจ type, range (`amount > 0`), date format, reserved category (`recurring`), duplicate protection
    4. **Human-in-the-Loop (Blocking Confirmation Modal / Inline Card):** บล็อก input แชท (`inputLocked`) จนกว่าผู้ใช้จะตรวจสอบ/แก้ไขและกดยืนยันเอง (+ มีปุ่ม Undo หลังบันทึก)
    5. **Execution Bounds (`step < 4`, Timeout 45s/4s, Retry Cap 2):** กัน infinite loop และ runaway API cost

### 6.3 ศัพท์และแนวคิดสำคัญของวิชาที่หยิบมาผูกกับ Finora ได้ทันที
- **Grounding / Tool-Augmented Computation:** ไม่ให้ LLM คิดเลขเอง แต่ให้เรียก `computeFinancialInsights` / `runReadTool` เพื่อลด **Hallucination**
- **Structured Outputs & Defensive Parsing:** ใช้ `response_format: { type: "json_object" }` + `temperature: 0` + parser ที่ทนทานต่อ markdown fence และประเภทข้อมูลเพี้ยน
- **Graceful Degradation:** OCR.space ล่ม/timeout 4 วิ → fallback เป็น Pure Vision LLM โดยระบบยังทำงานต่อได้
- **Context Engineering & Token Efficiency:** ส่งประวัติแชทแค่ 8 ข้อความล่าสุด + Compact Insights Summary + รองรับ `sections` parameter ใน `get_spending_insights` เพื่อลด context bloat
- **Self-Correction Loop:** เมื่อ LLM ส่ง JSON arguments ผิด หรือลืมส่ง `tool_calls` (fake confirm) ระบบส่ง feedback message กลับเข้า conversation ให้โมเดลแก้ตัวเองในรอบถัดไป

---

## 7. Quick-Reference Cheat Sheet (เปิดดูไวก่อนเข้าห้องสอบ)

| คำถามสั้น | คำตอบเป๊ะ + ไฟล์อ้างอิง |
| :--- | :--- |
| Agent loop วิ่งได้กี่รอบ? | สูงสุด **4 รอบ** (`for (let step = 0; step < 4; step++)`) — `src/app/(app)/ai-assistant/page.tsx:681` |
| ส่งประวัติแชทย้อนหลังกี่ข้อความ? | **8 ข้อความล่าสุด** (`.slice(-8)`) — `src/app/(app)/ai-assistant/page.tsx:960` |
| OpenRouter timeout & retry กี่ครั้ง? | Timeout **45 วินาที**, Retry **2 ครั้ง** (`1s`, `2s`) เฉพาะ `429/502/503/504` หรือ network error (ไม่ retry timeout) — `src/lib/openrouter.ts:41–43, 131, 181` |
| OCR.space timeout & max size? | Timeout **4 วินาที**, Max base64 **1,350,000 chars (~1 MB)**, พังแล้วคืน `undefined` — `src/lib/ocr.ts:2–3, 20–25` |
| Slip image ย่อขนาดเท่าไหร่? | ส่งตรงถ้า `<= 950 KB` & `<= 1600px`; ไม่งั้นย่อ `1600px` JPEG quality `0.88→0.78→0.68`, thumbnail `180px` — `src/lib/slip-image.ts:1–3, 58–71` |
| มี AI Tool กี่ตัว? | **10 ตัว** = 6 Read (🟢) + 4 Write (🔴) — `src/lib/ai-tools.ts:24–61` |
| Priority order ตอนเรียกหลาย tool? | `0: Read` → `1: add_category` → `2: income tx` → `3: budget/recurring` → `4: expense tx` — `src/lib/ai-tools.ts:64–74` |
| Prompt version ปัจจุบัน? | **`2.10.0`** — `src/lib/prompts.ts:4` |
| รัน Test อย่างไร ได้ผลเท่าไหร่? | `npm run test:check` → **30/30 checks passed** — `src/lib/self-check.ts` |
