from flask import Flask, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

# Directly embedded precomputed JSON data
precomputed_data = [
    {
        "tag": "python",
        "data": [
            {"year": 2021, "popularity": 5.995283488889627},
            {"year": 2022, "popularity": 5.663853559966718},
            {"year": 2023, "popularity": 4.370013888440296},
            {"year": 2024, "popularity": 4.035087719298246},
            {"year": 2025, "popularity": 3.7785444389682934}
        ]
    },
    {
        "tag": "javascript",
        "data": [
            {"year": 2021, "popularity": 3.8031022685754143},
            {"year": 2022, "popularity": 3.7560917627481283},
            {"year": 2023, "popularity": 2.826136106714899},
            {"year": 2024, "popularity": 2.224310776942356},
            {"year": 2025, "popularity": 1.974700180712995}
        ]
    },
    {
        "tag": "java",
        "data": [
            {"year": 2021, "popularity": 1.8201747102002854},
            {"year": 2022, "popularity": 1.6700344704623795},
            {"year": 2023, "popularity": 1.6246245276315365},
            {"year": 2024, "popularity": 1.5413533834586466},
            {"year": 2025, "popularity": 1.7151306062099556}
        ]
    },
    {
        "tag": "reactjs",
        "data": [
            {"year": 2021, "popularity": 1.876639984056864},
            {"year": 2022, "popularity": 2.0266254605966956},
            {"year": 2023, "popularity": 1.80549723846129},
            {"year": 2024, "popularity": 1.2280701754385965},
            {"year": 2025, "popularity": 1.2354197470018071}
        ]
    },
    {
        "tag": "c#",
        "data": [
            {"year": 2021, "popularity": 1.4614541468761417},
            {"year": 2022, "popularity": 1.5571139902531794},
            {"year": 2023, "popularity": 1.5180388230354316},
            {"year": 2024, "popularity": 1.5977443609022555},
            {"year": 2025, "popularity": 1.448989650073928}
        ]
    },
    {
        "tag": "android",
        "data": [
            {"year": 2021, "popularity": 1.0196964161158535},
            {"year": 2022, "popularity": 1.0341138713895162},
            {"year": 2023, "popularity": 1.094925874487258},
            {"year": 2024, "popularity": 1.3157894736842104},
            {"year": 2025, "popularity": 1.172991621488418}
        ]
    },
    {
        "tag": "html",
        "data": [
            {"year": 2021, "popularity": 1.4680971202710333},
            {"year": 2022, "popularity": 1.1351479852609057},
            {"year": 2023, "popularity": 1.1433739220309422},
            {"year": 2024, "popularity": 0.8521303258145364},
            {"year": 2025, "popularity": 0.7294233612617053}
        ]
    },
    {
        "tag": "r",
        "data": [
            {"year": 2021, "popularity": 1.1558773707111303},
            {"year": 2022, "popularity": 1.2599548318079163},
            {"year": 2023, "popularity": 0.959271341364943},
            {"year": 2024, "popularity": 0.8521303258145364},
            {"year": 2025, "popularity": 0.7162805979957286}
        ]
    },
    {
        "tag": "c++",
        "data": [
            {"year": 2021, "popularity": 1.026339389510745},
            {"year": 2022, "popularity": 0.9330797575181268},
            {"year": 2023, "popularity": 0.8365362875876102},
            {"year": 2024, "popularity": 0.7142857142857143},
            {"year": 2025, "popularity": 1.2025628388368654}
        ]
    },
    {
        "tag": "flutter",
        "data": [
            {"year": 2021, "popularity": 1.0230179028132993},
            {"year": 2022, "popularity": 0.8558183763223582},
            {"year": 2023, "popularity": 0.8946739446400309},
            {"year": 2024, "popularity": 0.9962406015037595},
            {"year": 2025, "popularity": 0.7951371775915885}
        ]
    }
]

@app.route('/api/tags', methods=['GET'])
def get_precomputed_tag_trends():
    try:
        return jsonify(precomputed_data)
    except Exception as e:
        app.logger.error(f"Failed to return precomputed data: {str(e)}", exc_info=True)
        return jsonify({"error": "An internal error occurred while loading tag data."}), 500
import os

if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    app.run(debug=False, host='0.0.0.0', port=port)


