package OLink.bpm.base.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class SessionSignal {

	public Session currentSession = null;

	public Transaction currentTransaction = null;

	public int transactionSignal = 0;
	
//	public int sessionSignal = 0;
}
