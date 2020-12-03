package com.tmobile.pacman.commons.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

import com.tmobile.pacman.commons.PacmanSdkConstants;
import com.tmobile.pacman.commons.exception.InvalidInputException;

public class IPUtils {
	
	private IPUtils() {
	    throw new IllegalStateException("Utility class");
	}
	
	private static final String IP_ALL = "*,any,Any,Internet,0.0.0.0";
		
	/**
	 * checks whether the range has non permitted IPs
	 * @param startIP
	 * @param endIP
	 * @param permittedIps
	 * @return true if there is any non permitted IPs between the given start IP and end IP
	 */
	public static boolean hasNonPermittedIPs(String startIP, String endIP, List<String> permittedIps) {
		long startIPInLong = ipToLong(startIP);
		long endIPInLong = ipToLong(endIP);
		for(long ipToTest=startIPInLong; ipToTest<= endIPInLong; ipToTest++) {
			if(!isPermittedIP(permittedIps, ipToTest)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * check whether the given IP comes in any of the permitted ranges
	 * @param cidrRanges
	 * @param ipToTest
	 * @return true if the ip to test comes in any of the permitted ranges
	 */
	public static boolean isPermittedIP(List<String> cidrRanges, long ipToTest) {
		for(String cidr : cidrRanges ) {
			String[] range = getRangeFromCidr(cidr).split(PacmanSdkConstants.HYPHEN);
			
			long rangeStart = ipToLong(range[0]);
			long rangeEnd = ipToLong(range[1]);
						
			if(ipToTest >= rangeStart && ipToTest <= rangeEnd) {
				return true;
			}
		}
		return false;
	}

	/**
	 * checks whether given range is in permitted range
	 * @param startIP
	 * @param endIP
	 * @param cidrRange
	 * @return true if start IP and end IP are in permitted range
	 */
	public static boolean isInRange(String startIP, String endIP, String cidrRange) {
		SubnetUtils utils = new SubnetUtils(cidrRange);
		utils.setInclusiveHostCount(true);
		SubnetInfo subnetInfo =utils.getInfo();
		return (subnetInfo.isInRange(startIP)&& subnetInfo.isInRange(endIP));
	}
	
	/**
	 * checks whether given IP notation is a single IP or cidr with subnet mask 32
	 * 
	 * @param ipNotation
	 * @return true if the IP notation is single IP or a cidr with subnet 32
	 */
	public static boolean isSingleIP(String ipNotation) {
		boolean isSingleIp = false;
		if(isACidr(ipNotation)) {
			String[] cidrSplit = ipNotation.split(PacmanSdkConstants.BACK_SLASH);
			if(cidrSplit.length==2 && Integer.parseInt(cidrSplit[1])==32) {
				isSingleIp = true;
			}
		} else {
			isSingleIp =true;
		}
		return isSingleIp;
	}
	
	/**
	 * Get start IP and end IP from given ipAddress
	 * ipAddress can be cidr or a valid IP
	 * @param ipAddress
	 * @return start IP and end IP from given ipAddress in the format - rangeStartIP-rangeEndIP
	 */
	public static String getRangeFromCidr(String ipAddress){
		SubnetUtils utils =null;
		String rangeStart;
		String rangeEnd;
		if(isACidr(ipAddress)) {
			utils = new SubnetUtils(ipAddress);
			utils.setInclusiveHostCount(true);
			rangeStart = utils.getInfo().getLowAddress();
			rangeEnd = utils.getInfo().getHighAddress();
		} else {
			rangeStart = ipAddress;
			rangeEnd = ipAddress;
		}
		return rangeStart+PacmanSdkConstants.HYPHEN+rangeEnd;
	}
	
	/**
	 * Gets the long representation of the 32 bit IP address
	 * @param ip
	 * @return long representation of the 32 bit IP address
	 */
	public static long ipToLong(String ip) {
		try {
			InetAddress inet = InetAddress.getByName(ip);
			byte[] octets = inet.getAddress();
	        long result = 0;
	        for (byte octet : octets) {
	            result <<= 8;
	            result |= octet & 0xff;
	        }
	        return result;
		} catch (UnknownHostException e) {
			throw new InvalidInputException(e.getMessage());
		}
    }
	
	/**
	 * Check whether given ip address is a CIDR
	 * @param ip
	 * @return true if given ip address is a CIDR
	 */
	public static boolean isACidr(String ip) {
		return ip.contains(PacmanSdkConstants.BACK_SLASH);
	}
	
	/**
	 * subnet mask must be greater than 0 and less than equal to 32
	 * @param cidr
	 * @return true if subnet mask is greater than 0 and less than equal to 32
	 */
	public static boolean isValidSubnetMask(String cidr) {
		if(cidr.contains(PacmanSdkConstants.BACK_SLASH)) {
			String[] cidrSplit = cidr.split(PacmanSdkConstants.BACK_SLASH);
			if(cidrSplit.length==2) {
				int subnetMask = Integer.parseInt(cidrSplit[1]);
				return (subnetMask > 0 && subnetMask <= 32);
			}
		}
		return false;
	}

	/**
	 * checks if the IP range is outside the permissible public IP ranges
	 * 
	 * convert the 32 bit IP address to corresponding long representation
	 * iterate through each long number within IP range and check whether it is in permissible range
	 * if any one is not in valid range return false
	 * 
	 * @param start ip
	 * @param end ip
	 * @param allowedCidrIps
	 * @return 
	 */
	public static boolean isAddressInPermissibleRange(String startIP, String endIP, List<String> permittedRanges) {
		
		//cosmos db and storage accounts have only IP or cidr ranges
		//other RDBs have start IP and end IP
		// for CIDR and single IP, we are assigning end IP
		if(endIP == null) {
			if(isACidr(startIP) && !isValidSubnetMask(startIP)) {
				//azure portal allows the subnet mask 0, but utils method throws error
				// subnet mask 0 means all IPs ie 0.0.0.0 - 255.255.255.255
				//we are directly returning the firewall rule as not in permitted range.				
				return false;
			} else {
				String[] range = getRangeFromCidr(startIP).split(PacmanSdkConstants.HYPHEN);
				startIP = range[0];
				endIP = range[1];
			}
		} 
		List<String> permittedIps = new ArrayList<>();
		for(String permittedRange : permittedRanges) {
			if(!isSingleIP(permittedRange)) {
				if(isInRange(startIP, endIP, permittedRange)) {
					return true;
				}
			} else {
				String ip = isACidr(permittedRange)?permittedRange.split(PacmanSdkConstants.BACK_SLASH)[0]:permittedRange;
				permittedIps.add(ip);
			}
		}		
		return !hasNonPermittedIPs(startIP, endIP, permittedIps);
	}
	
	/**
	 * Checks whether IP is a valid IPv4
	 * IP must be in the format x.x.x.x/y or x.x.x.x
	 * where x is 0 <= x <= 255 and y is 0 <= y <= 32
	 * @param ip
	 * @return
	 */
	public static boolean validIPv4(String address) {
		boolean isValid = true;
	    try {
	        if ( address == null || address.isEmpty() || address.endsWith(".")) {
	        	return false;
	        }
	        String ip = isACidr(address)? address.split(PacmanSdkConstants.BACK_SLASH)[0]:address;
	        String[] parts = ip.split( "\\." );
	        if ( parts.length != 4 ) {
	        	isValid = false;
	        }

	        for ( String s : parts ) {
	            int i = Integer.parseInt( s );
	            if ( (i < 0) || (i > 255) ) {
	            	isValid = false;
	            }
	        }
	        
	        String maskStr = isACidr(address)? address.split(PacmanSdkConstants.BACK_SLASH)[1]:"0";
	        int mask = Integer.parseInt(maskStr);
	        if ( (mask < 0) || (mask > 32) ) {
            	isValid = false;
            }
	        
	    } catch (NumberFormatException nfe) {
	    	isValid = false;
	    }
	    return isValid;
	}
	
	/**
	 * checks whether the given address is public or not by checking following,
	 * 1. Whether the address is * or Any or Internet
	 * 2. Whether the address is not in given allowed cidr range
	 * @param address
	 * @param allowedCidrs
	 * @return true if address is public
	 */
	public static boolean isNsgPublicAddress(String address, List<String> allowedCidrs) {
		boolean isPublic = false;
		List<String> publicIps = new ArrayList<>(Arrays.asList(IP_ALL.split(PacmanSdkConstants.COMMA)));
		if(publicIps.contains(address)) {
			isPublic=true;
		} else if(validIPv4(address)){
			isPublic = !IPUtils.isAddressInPermissibleRange(address, null, allowedCidrs);
		}
		return isPublic;
	}
	
	/**
	 * checks whether the given addresses are public or not by checking following,
	 * 1. Whether the address is * or Any or Internet
	 * 2. Whether the address is not in given allowed cidr range
	 * @param addresses
	 * @param allowedCidrs
	 * @return public addresses
	 */
	public static Set<String> filterPublicAddressOutsidePermittedRange(List<String> addresses, List<String> allowedCidrs) {
		Set<String> publicAddresses = new HashSet<>();
		addresses.stream().forEach(address -> {
			List<String> publicIps = new ArrayList<>(Arrays.asList(IP_ALL.split(PacmanSdkConstants.COMMA)));
			if (publicIps.contains(address) || (validIPv4(address)
					&& !isAddressInPermissibleRange(address, null, allowedCidrs))) {
				publicAddresses.add(address);
			} 
		});
		return publicAddresses;
	}
	
	/**
	 * checks whether the addresses are not in given cidr range
	 * @param addresses
	 * @param allowedCidrs
	 * @return addresses outside given range
	 */
	public static Set<String> filterAddressOutsideGivenRanges(List<String> addresses, List<String> givenRanges) {
		Set<String> outRangeAddresses = new HashSet<>();
		List<String> publicIps = new ArrayList<>(Arrays.asList(IP_ALL.split(PacmanSdkConstants.COMMA)));
		if(givenRanges.stream().anyMatch(ip->publicIps.contains(ip))){
			return new HashSet<>();
		}
		addresses.stream().forEach(address -> {
			if (validIPv4(address)) {
				if(!isAddressInPermissibleRange(address, null, givenRanges)) {
					outRangeAddresses.add(address);
				}
			} else {
				if(publicIps.contains(address) &&
					givenRanges.stream().noneMatch(ip->publicIps.contains(ip))) {
					outRangeAddresses.add(address);
				}
			}
		});
		return outRangeAddresses;
	}
	

	/**
	 * Get common ranges from an IP and set of IPs
	 * @param ipAddress1
	 * @param ipAddresses
	 * @return
	 */
	public static String getCommonRange(String ipAddress1, Set<String> ipAddresses) {
		for(String ipAddress2: ipAddresses) {
			List<String> publicIps = new ArrayList<>(Arrays.asList(IP_ALL.split(PacmanSdkConstants.COMMA)));
			if(publicIps.contains(ipAddress2)) {
				return ipAddress1;
			} else {
				String overlappingRange = getOverLappingRange(ipAddress1, ipAddress2);
				if(null!=overlappingRange) {
					return overlappingRange;
				}
			}
		}
		return null;
	}

	/**
	 * Get overlapping ip range between the given ranges
	 * @param ipRange1
	 * @param ipRange2
	 * @return overlapping range between ipRange 1 and 2
	 */
	public static String getOverLappingRange(String ipRange1, String ipRange2) {
		String overlappingRange = null;
		String[] nicrange = getRangeFromCidr(ipRange1).split(PacmanSdkConstants.HYPHEN);
		long nicrangeStart = ipToLong(nicrange[0]);
		long nicrangeEnd = ipToLong(nicrange[1]);
		
		String[] subnetrange = getRangeFromCidr(ipRange2).split(PacmanSdkConstants.HYPHEN);
		long subnetrangeStart = ipToLong(subnetrange[0]);
		long subnetrangeEnd = ipToLong(subnetrange[1]);
		
		if(nicrangeStart <= subnetrangeEnd && subnetrangeStart <= nicrangeEnd) {
			String startIP = nicrangeStart>subnetrangeStart?nicrange[0]:subnetrange[0];
			String endIP = nicrangeEnd<subnetrangeEnd?nicrange[1]:subnetrange[1];
			overlappingRange = startIP+PacmanSdkConstants.HYPHEN+endIP;						
		}
		return overlappingRange;
	}

}
