/**
 * Currency Service Mock
 */
async function getExchangeRate(from, to) {
  const rates = { 'USD_THB': 35.5, 'THB_USD': 0.028 };
  return rates[`${from}_${to}`] || 1.0;
}

async function convertCurrency(amount, from, to) {
  const rate = await getExchangeRate(from, to);
  return amount * rate;
}

module.exports = { getExchangeRate, convertCurrency };
