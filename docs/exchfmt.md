# Exchange format of the data between the processes.

Here is the sample format we decided on 16-Sep-2017. It needs to improved. Suggestions are welcome!

Keep it adaptive such that even though we add some more features for our search like we gonna produce some image results too then this must be easier to manage.

    ````json
    /* Here is a template */

    {
        "title": "fruits",
        "date_visited": "dd-mm-yyyy",
        "keywords": [
            "some", "keywords", "from", "meta", "of", "the", "page", "like", "category", "of", "the", "page"
        ],
        "contents": [
            {
                "subtitle": "from semantics of the markup",
                "mime_type": "img",
                "data": "xy.in/a.png"
            },
            {
                "subtitle": "sweet-fruits",
                "mime-type": "text",
                "data": "This is a bluff describing the sweet fruits"
            },
            {
                "subtitle": "fruit-code",
                "mime-type": "code",
                "data": "int main() { return 0; }"
            }
        ]
    }

    ````