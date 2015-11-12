package visualharvester.service;

public enum PairKey {
	VAULT_URL("vaultUrl"), TELLER_URL("tellerUrl"), WORKFLOW_STORE_URL("workflowsUrl"), WORKFLOW_PROCESSOR_URL(
			"processorUrl"), WORKFLOW_MANAGER_URL("managerUrl");

	String value;

	PairKey(String value) {
		this.value = value;
	}

	public String getKey() {
		return value;
	}
}