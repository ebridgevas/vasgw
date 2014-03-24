package com.ebridgecommerce.services;

import com.ebridgecommerce.db.DBAdapter;
import com.ebridgecommerce.domain.TxnType;
import com.ebridgecommerce.prepaid.client.PPSClient;
import com.ebridgecommerce.exceptions.TransactionFailedException;

public class ClassOfServiceRequestProcessor {

	private static ClassOfServiceRequestProcessor instance;
	
	private PPSClient ppsClient;
	private DBAdapter db;

	protected ClassOfServiceRequestProcessor(PPSClient ppsClient, DBAdapter db) {
		this.ppsClient = ppsClient;
		this.db = db;
	}

	public static ClassOfServiceRequestProcessor getInstance(PPSClient ppsClient, DBAdapter db ) {
		if (instance == null) {
			instance = new ClassOfServiceRequestProcessor(ppsClient, db);
		}
		return instance;
	}
	public String processSMS(String subscriberId, String serviceCommand) {
		String[] tokens = serviceCommand.split("#");
		if (tokens.length == 1) {
			try {
				String cosName = ppsClient.getClassOfService(subscriberId);
				log(subscriberId, cosName, "COS_QUERY");
				return "You are being billed per "
						+ ("TEL_COS".equals(cosName) ? "minute. To change to per second, send tariff#second to 23350"
								: " second. To change to per minute, send tariff#minute to 23350");
			} catch (TransactionFailedException ex) {
				try {
					logError(subscriberId, "", ex.getMessage());
					return ex.getMessage();
				} catch (Exception e) {
					return "Transaction not completed. Please try again.";
				}
			}
		} else if ((tokens.length > 1) && tokens[1].toLowerCase().trim().startsWith("sec")) {
			Boolean subscriberLogExists = Boolean.FALSE;
			try {
				if (Integer.parseInt(db.getString("SELECT number_of_changes_this_month FROM subscriber_cos WHERE subscriberId = '" + subscriberId + "'")) > 1) {
					try {
						return "You can only change you Tariff Plan twice per month.";
					} catch (Exception e) {
						return "Transaction not completed. Please try again.";
					}
				} else {
					subscriberLogExists = Boolean.TRUE;
				}
			} catch (Exception e) {
			}
			try {
				ppsClient.setClassOfService(subscriberId, "PERSEC_COS");
				log(subscriberLogExists, subscriberId, "Per Second");
				return "You are now being billed per second.";
			} catch (TransactionFailedException ex) {
				try {
					logError(subscriberId, "Per Second", ex.getMessage());
					return ex.getMessage();
				} catch (Exception e) {
					return "Transaction not completed. Please try again.";
				}
			}
		} else if ((tokens.length > 1) && tokens[1].toLowerCase().trim().startsWith("min")) {
			Boolean subscriberLogExists = Boolean.FALSE;
			try {
				if (Integer.parseInt(db.getString("SELECT number_of_changes_this_month FROM subscriber_cos WHERE subscriberId = '" + subscriberId + "'")) > 1) {
					try {
						return "You can only change you Tariff Plan twice per month.";
					} catch (Exception e) {
						return "Transaction not completed. Please try again.";
					}
				} else {
					subscriberLogExists = Boolean.TRUE;
				}
			} catch (Exception e) {
			}
			try {
				ppsClient.setClassOfService(subscriberId, "TEL_COS");
				log(subscriberLogExists, subscriberId, "Per Minute");
				return "You are now being billed per minute.";
			} catch (TransactionFailedException ex) {
				try {
					logError(subscriberId, "", ex.getMessage());
					return ex.getMessage();
				} catch (Exception e) {
					return "Transaction not completed. Please try again.";
				}
			}
		} else {
			return "Invalid tariff change command";
		}
	}

	public String processUSSD(String subscriberId, Integer transactionId, String[] msg) {

		if ("80".equals(msg[0])) { /* USSD Request */
			/* 80 12334 175# */
			/* 80 12334 175*1# */
			/* 80 12334 175*2# */
			String tokens[] = msg[2].split("\\*");
			for (int i = 0; i < tokens.length; ++i) {
				System.out.println(i + " = " + tokens[i]);
			}
			String text;
			if (tokens.length == 1) { /* getClassOfService */
				try {
					String cosName = ppsClient.getClassOfService(subscriberId);
					log(subscriberId, cosName, TxnType.COS_QUERY);
					text = "You are currently being billed per " + ("TEL_COS".equals(cosName) ? "minute" : "second") + "\n";
					if ("TEL_COS".equals(cosName)) {
						// You are now currently being billed per minutePress: (1) C to
						// change to per second billing (2) C to end
						text += "Reply/Answer: \n(1) to change to per-second billing \n(2) to end";
					} else {
						text += "Reply/Answer: \n(1) to change to per-minute billing \n(2) to end";
					}
					return "72" + " " + transactionId + " " + 30000 + " 0 " + text + "."; /*
																																								 * with
																																								 * prompt
																																								 */
				} catch (TransactionFailedException ex) {
					try {
						return "81" + " " + transactionId + " " + " 0 " + ex.getMessage() + ".";
					} catch (Exception e) {
						return "81" + " " + transactionId + " " + " 0 " + "Transaction not completed. Please try again.";
					}
				}

			} else if (tokens.length == 2) {
				if ("1".equals(tokens[1])) { /* setClassOfService */
					Boolean subscriberLogExists = Boolean.FALSE;
					try {
						try {
							if (Integer.parseInt(db.getString("SELECT number_of_changes_this_month FROM subscriber_cos WHERE subscriberId = '" + subscriberId + "'")) > 1) {
								try {
									return "81" + " " + transactionId + " " + "0 You can only change you Tariff Plan twice per month.";
								} catch (Exception e) {
									return "81" + " " + transactionId + " " + "0 " + "Transaction not completed. Please try again.";
								}
							} else {
								subscriberLogExists = Boolean.TRUE;
							}
						} catch (Exception e) {
						}
						String cosName = ppsClient.getClassOfService(subscriberId);
						if ("TEL_COS".equals(cosName)) { /* Toggle */
							cosName = "PERSEC_COS";
						} else {
							cosName = "TEL_COS";
						}

						ppsClient.setClassOfService(subscriberId, cosName);

						String newCosName = ppsClient.getClassOfService(subscriberId);

						text = "Thank you for using Telecel Tariff Plan Selection Service! ";
						if ("TEL_COS".equals(newCosName)) {
							// log( subscriberLogExists, subscriberId, "Per minute" );
							text += "You are now being billed per minute.";
						} else {
							// log( subscriberLogExists, subscriberId, "Per Second" );
							text += "You are now being billed per second.";
						}

						return "81" + " " + transactionId + " " + "0 " + text + "."; /*
																																					 * no
																																					 * prompt
																																					 */
					} catch (TransactionFailedException ex) {
						try {
							return "81" + " " + transactionId + " " + "0 " + ex.getMessage() + ".";
						} catch (Exception e) {
							return "81" + " " + transactionId + " " + "0 " + "Transaction not completed. Please try again.";
						}
					}
				} else { /* getClassOfService, no prompt */
					try {
						String cosName = ppsClient.getClassOfService(subscriberId);
						text = "Thank you for using Telecel Tariff Plan Selection Service! ";
						if ("TEL_COS".equals(cosName)) {
							log(subscriberId, cosName, TxnType.COS_QUERY);
							text += "You are being billed per minute.";
						} else {
							log(subscriberId, cosName, TxnType.COS_QUERY);
							text += "You are being billed per second.";
						}
						return "81" + " " + transactionId + " " + "0 " + text + "."; /*
																																					 * no
																																					 * prompt
																																					 */
					} catch (TransactionFailedException ex) {
						try {
							return "81" + " " + transactionId + " " + "0 " + ex.getMessage() + ".";
						} catch (Exception e) {
							return "81" + " " + transactionId + " " + " 0 " + "Transaction not completed. Please try again.";
						}
					}
				}
			} else {
				return "81" + " " + transactionId + " " + " 0 Invalid answer.";
			}
		} else if ("74".equals(msg[0])) { /* USSD Answer */
			/* 74 34278 0 0 0 0 15 2 */
			String text;
			if ("1".equals(msg[7])) { /* setClassOfService */
				Boolean subscriberLogExists = Boolean.FALSE;
				try {
					String cosName = ppsClient.getClassOfService(subscriberId);
					if ("TEL_COS".equals(cosName)) { /* Toggle */
						cosName = "PERSEC_COS";
					} else {
						cosName = "TEL_COS";
					}
					try {
						if (Integer.parseInt(db.getString("SELECT number_of_changes_this_month FROM subscriber_cos WHERE subscriberId = '" + subscriberId + "'")) > 1) {
							try {
								return "81" + " " + transactionId + " " + "0 You can only change you Tariff Plan twice per month.";
							} catch (Exception e) {
								return "81" + " " + transactionId + " " + "0 " + "Transaction not completed. Please try again.";
							}
						} else {
							subscriberLogExists = Boolean.TRUE;
						}
					} catch (Exception e) {
					}
					ppsClient.setClassOfService(subscriberId, cosName);

					String newCosName = ppsClient.getClassOfService(subscriberId);

					text = "Thank you for using Telecel Tariff Plan Selection Service! ";
					if ("TEL_COS".equals(newCosName)) {
						text += "You are now being billed per minute.";
						log(subscriberLogExists, subscriberId, "Per minute");
					} else {
						text += "You are now being billed per second.";
						log(subscriberLogExists, subscriberId, "Per second");
					}
					return "81" + " " + transactionId + " " + "0 " + text + "."; /*
																																				 * no
																																				 * prompt
																																				 */
				} catch (TransactionFailedException ex) {
					try {
						return "81" + " " + transactionId + " " + "0 " + ex.getMessage() + ".";
					} catch (Exception e) {
						return "81" + " " + transactionId + " " + "0 " + "Transaction not completed. Please try again.";
					}
				}
			} else {
				try {
					String cosName = ppsClient.getClassOfService(subscriberId);
					text = "Thank you for using Telecel Tariff Plan Selection Service! ";
					if ("TEL_COS".equals(cosName)) {
						text += "You are being billed per minute.";
					} else {
						text += "You are being billed per second.";
					}
					return "81" + " " + transactionId + " " + "0 " + text + "."; /*
																																				 * no
																																				 * prompt
																																				 */
				} catch (TransactionFailedException ex) {
					try {
						return "81" + " " + transactionId + " " + "0 " + ex.getMessage() + ".";
					} catch (Exception e) {
						return "81" + " " + transactionId + " " + "0 " + "Transaction not completed. Please try again.";
					}
				}
			}
		} else {
			return "81" + " " + transactionId + "0 " + "Invalid answer.";
		}
	}

	public String processUSSD(String subscriberId, String serviceCommand) {
		String[] tokens = serviceCommand.split("#");
		if (tokens.length == 1) {
			try {
				String cosName = ppsClient.getClassOfService(subscriberId);
				return "You are being billed per "
						+ ("TEL_COS".equals(cosName) ? "minute. To change to per second, send tariff#second to 23350"
								: " second. To change to per minute, send tariff#minute to 23350");
			} catch (TransactionFailedException ex) {
				try {
					return ex.getMessage();
				} catch (Exception e) {
					return "Transaction not completed. Please try again.";
				}
			}
		} else if ((tokens.length > 1) && tokens[1].toLowerCase().startsWith("sec")) {
			try {
				ppsClient.setClassOfService(subscriberId, "PERSEC_COS");
				return "You are now being billed per second.";
			} catch (TransactionFailedException ex) {
				try {
					return ex.getMessage();
				} catch (Exception e) {
					return "Transaction not completed. Please try again.";
				}
			}
		} else if ((tokens.length > 1) && tokens[1].toLowerCase().startsWith("min")) {
			try {
				ppsClient.setClassOfService(subscriberId, "TEL_COS");
				return "You are now being billed per minute.";
			} catch (TransactionFailedException ex) {
				try {
					return ex.getMessage();
				} catch (Exception e) {
					return "Transaction not completed. Please try again.";
				}
			}
		} else {
			return "Invalid tariff change command";
		}
	}

	private void log(Boolean subscriberLogExists, String subscriberId, String newCosName) {

		log(subscriberId, newCosName, "COS_CHANGE");
		String sql = "";
		if (subscriberLogExists) {
			sql = "UPDATE subscriber_cos SET number_of_changes_this_month =  number_of_changes_this_month + 1, last_change_date = now() WHERE subscriberId = '"
					+ subscriberId + "'";
			db.update(sql);
			System.out.println(sql);
		} else {
			sql = " INSERT INTO subscriber_cos (subscriberId, cos_name, last_change_date, number_of_changes_this_month )";
			sql += " VALUES ('" + subscriberId + "','" + newCosName + "',now(),1 )";
			System.out.println(sql);
			db.update(sql);
		}
	}

	private void log(String subscriberId, String newCosName, String txnType) {
		String sql = "INSERT INTO subscriber_cos_logs ( uuid, subscriberId, service_command, cos_name, transaction_date, transaction_result, narrative, channel )";
		sql += " VALUES (" + System.currentTimeMillis() + ",'" + subscriberId + "','" + txnType + "','" + newCosName + "',now(),'successful','successful','sms' )";
		System.out.println(sql);
		try {
			db.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logError(String subscriberId, String newCosName, String narrative) {
		String sql = "INSERT INTO subscriber_cos_logs ( uuid, subscriberId, service_command, cos_name, transaction_date, transaction_result, narrative )";
		sql += " VALUES (" + System.currentTimeMillis() + ",'" + subscriberId + "','COS_QUERY','" + newCosName + "',now(),'error','" + narrative + "' )";
		System.out.println(sql);
		try {
			db.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void log(String subscriberId, String newCosName, TxnType txnType) {
		String sql = "INSERT INTO subscriber_cos_logs ( uuid, subscriberId, service_command, cos_name, transaction_date, transaction_result, narrative, channel )";
		sql += " VALUES (" + System.currentTimeMillis() + ",'" + subscriberId + "','" + txnType + "','" + newCosName + "',now(),'successful','successful','ussd' )";
		System.out.println(sql);
		try {
			db.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
