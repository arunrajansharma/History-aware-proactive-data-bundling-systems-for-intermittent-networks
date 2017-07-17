"""
File Name: history_analysis.py
Deployement : AWS EC2 instance
Purpose : This process, spawned by the Nodejs process, receives the user identifier and picks up the respective 
		  browsing history of the user(in json format) and process it.
		  It sorts the history items on the number of visits and the time of visit, the former being given the more 
		  priority.
		  Once the list of top 3 Url is generated, those 3 URLs are accessed and saved in that particular 
		  user's directory with the option of compression.
		  Finally, it returns the success/error to the Node.js code.

Course  : CSE 534 - Fundamental of Computer Networks.

"""
import json
import urllib2
import zipfile
import sys

"""
This function finds out which urls are most relevant
"""
def get_top_urls(in_json):
	out_urls = {}
	data = []
	jsons = []
	jsons = in_json.split('\n')
	for line in jsons:
		if len(line) == 0:
			break
		url_info = json.loads(line)
		data.append([url_info['url'], url_info['datetime'], url_info['visits']])
	
	data.sort(key=lambda x: x[0][2], reverse=True)
	out_urls['url_1.html'] = data[0][0]
	out_urls['url_2.html'] = data[1][0]
	out_urls['url_3.html'] = data[2][0]
	#s = json.dumps(out_urls)
	# print out_urls	
	return out_urls


"""
This function creates a zip file containing the web pages
"""
def compressAndStore(userId):
	print 'creating archive'
	zf = zipfile.ZipFile("./userFolder/"+userId+"/bundle.zip", mode='w')
	try:
		zf.write("./userFolder/url_1.html")
		zf.write("./userFolder/url_2.html")
		zf.write("./userFolder/url_3.html")
	finally:
		zf.close()


"""
Main function that downloads the top urls and stores it in the respective folders
"""
def main(arg):
	urlList = get_top_urls(open("./userFolder/"+arg+"/training_config.json").read())
	for key in urlList:
		print urlList[key]
		response = urllib2.urlopen(urlList[key])
		#response = wget.download(url)
		webContent = response.read()
		#f = open("./userFolder/"+arg+"/"+key, 'w')
		f = open("./userFolder/"+key, 'w')
		f.write(webContent)
		f.close
	compressAndStore(arg)
	print(arg)
		


if __name__ == "__main__":
    main(sys.argv[1])


        

