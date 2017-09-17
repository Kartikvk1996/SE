# Output of the se-processes

This doc explains the format of the output the se-processes output which going to help the ux-designer to present the output in an engaging way. The below format is `json` and format may be change over time. So be adaptive while designing and coding the UI.

```` javascript
{
	"title" : "title of the search",
	"nresults" : "number of the results",
	"results" : [
		{
			"result-type" : "webpage",
			"proxy-link" : "http://our.se/proxylink",
			/* You need to get the title of page on your own. */
		},
		{
			"result-type" : "specific",
			"content" : [
				{
					"key" : "Like Age, Spouce",
					"value" : "32, XYZ",
					"weight" : 0.43434,	/* going to help while presenting the results. */
				}
			]
		}
	],
	/* Add more */
}
````
