package exception

class NoCSRFTokenException: RuntimeException("No CLRF token found on page")