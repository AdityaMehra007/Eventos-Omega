package com.example.data.model

data class BudgetSummary(
  val totalBudgetEstimatedINR: Long,
  val totalBudgetApprovedINR: Long,
  val totalCommittedINR: Long,
  val totalSpentINR: Long,
  val outstandingPayablesINR: Long,
  val contingencyBufferINR: Long,
  val categories: List<BudgetCategoryItem>
)

data class BudgetCategoryItem(
  val categoryName: String,
  val allocatedINR: Long,
  val spentINR: Long,
  val percentageUsed: Int
)

data class WorkerPayoutRecord(
  val id: String,
  val workerName: String,
  val eventTitle: String,
  val grossAmountINR: Long,
  val tdsDeductionINR: Long,
  val netPayableINR: Long,
  val status: String, // "PENDING_APPROVAL", "APPROVED", "DISBURSED_UPI", "SETTLED"
  val upiRefId: String,
  val date: String
)

data class InvoiceRecord(
  val id: String,
  val invoiceNumber: String, // e.g. "EVT-BLR-INV-0049"
  val clientName: String,
  val eventName: String,
  val subtotalINR: Long,
  val gst18INR: Long,
  val totalINR: Long,
  val status: String, // "PAID", "DUE_NET15", "OVERDUE"
  val dueDate: String
)
