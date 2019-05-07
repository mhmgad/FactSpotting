#!/usr/bin/env bash





sh assemble/bin/exper_cli.sh -i data/directed_sample_200.txt -o data/directed_sample_200_sentences_full.text -r directed -rp data/directed_paraphrases.txt -c S

sh assemble/bin/exper_cli.sh -i data/wasBornIn_sample_200.txt -o data/wasBornIn_sample_200_sentences_full.text -r wasBornIn -rp data/wasBornIn_paraphrases.txt -c SO