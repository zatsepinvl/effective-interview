# Page Parsing Task

This task simulates the challenge we have extracting speakable content from PDFs and Scanned Books in the Speechify Reader Apps.

It is impossible in practice to achieve 100% correctness when parsing these documents, due to the huge number of edge cases. When writing our parser we are always balancing user experience improvements, regression risk, and maintainability.

We have prepared a small test suite of real-world examples and simple feedback loop for you to iteratively build your solution.

# Concepts

- Before parsing, we extract a low-level representation from these pages. Each page affords a `List<WordWithBoundingBox>` that tells us the sequence of words and where each word appears on the page.
- Parsing takes these items and emits a `List<TextBlock>` that models the sequence "headings", "paragraphs", and "marginalia" that appear in the page.
- The `List<TextBlock>` flows downstream where we BOTH render it in our Classic Reader AND extract sentences for incremental TTS.

# Clarifications

1. The coordinates of the boxes are normalized and oriented such that `(0, 0)` is the top-left corner of the the page, and `(1, 1)` is the bottom-right corner of the page.
1. You can assume that the "marginalia" will NOT be rendered or spoken in the Speechify App
1. In general, you can trust the ordering of the `List<WordWithBoundingBox>` to reflect the correct ordering of reading. As always, there are some exceptions :)
1. Feel free to modify the `expected` outputs if you feel a different output would give a better user experience.

# Instructions

:warning: PLEASE DISABLE ALL AI CODING ASSISTANTS :warning:

1. Open this gradle project and run the tests. You can then compare the output in the test result window OR using the HTML comparison files under `src/test/resources/output`.
2. Add smarts to the `parsePage` function to address gaps between your output and the `expected` output
3. Prioritize the next gap to address and repeat!

# Evaluation

We will evaluate your solution qualitatively, looking at

1. the gaps between your output and the `expected` outputs
2. the decisions you made in structuring and implementing your parser logic in light of our robustness/maintainability design goals
3. the decisions you made about which edge cases to prioritize in light of their likely impact on the end user experience of reading and listening to the content
