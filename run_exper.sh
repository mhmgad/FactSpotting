#!/usr/bin/env bash





sh assemble/bin/exper_cli.sh -i directed_sample_200.txt -o directed_sample_200_sentences_full.text -r directed -rp directed_paraphrases.txt -b

sh assemble/bin/exper_cli.sh -i wasBornIn_sample_200.txt -o wasBornIn_sample_200_sentences_full.text -r wasBornIn -rp wasBornIn_paraphrases.txt -b