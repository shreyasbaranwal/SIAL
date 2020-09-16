---
title: 'SIAL: A simple image analysis library'
tags:
  - Java
  - Image Analysis
  - FIJI
  - ImageJ 
  - reproducbile science
authors:
  - name: David R. Tyrpak
    orcid: 0000-0002-2281-6129
    affiliation: 1
  - name: Yaocun Li
    affiliation: 1
  - name: Siqi Lei
    affiliation: 1
  - name: J. Andrew MacKay
    affiliation: "1,2,3"
affiliations:
  - name: Department of Pharmacology and Pharmaceutical Sciences, School of Pharmacy of the University of Southern California
    index: 1
  - name: Keck School of Medicine of the University of Southern California, Department of Ophthalmology, Roski Eye Institute
    index: 2
  - name: University of Southern California Viterbi School of Engineering, Biomedical Engineering
    index: 3
date: 16 September 2020
bibliography: paper.bib
---

# Summary

In many biomedical research labs, image analysis tasks are relatively simple but labor intensive. For example, a typical workflow may require human intervention to outline regions of interest or score phenotypes from a collection of images. Such tasks could potentially be partially automated with a script or machine learning, but in our experience, many biomedical researchers do not obtain programming skills. Furthermore, image analysis is typically just one of the many experiments that a busy researcher will employ during the course of a project, and so devoting time to learning programming or bespoke image analysis is not prioritized. These factors create an unfortunate situation where, in addition to being labor intensive, image analysis workflows may become biased and exhibit limited reproducibility.

# Statement of need

As primarily “wet-lab” scientists, we wanted to develop user friendly programs focused on the most common tasks that fellow wet-lab researchers encounter during image analysis. Because FIJI/ImageJ is routinely used by biomedical researchers the world over, we developed our programs in Java and packaged them together as a FIJI plugin. We name this plugin SIAL: A simple image analysis library. SIAL aids users in human-assisted image analysis by providing programs for image randomization, phenotype scoring, and ROI selection. Importantly, these programs automatically output useful metadata so that researchers can document their workflows. In addition, because wet-lab scientists are typically interrupted by multiple experiments, the programs keep track of which images in a directory have already been analyzed so that researchers can easily start and stop their workflows without hassle.

SIAL is easily installed via the ImageJ update website service, utilizes simple user interfaces, requires no programming experience, and requires no dependencies except FIJI. The individual programs inside SIAL can be easily integrated with workflows involving other FIJI plugins or with workflows employing another open source software, like Cell Profiler or QuPath. Because of its ease of use and focus on the most common wet-lab image analysis tasks, SIAL is routinely used by our group and our close collaborators to improve the efficieny and reproducibiltiy of our image analysis workflows.

# Acknowledgements

The development of SIAL was supported by grants RO1 GM114839 to J.A.M. and F31DK118881 to DRT. We thank Anh Truong for valuable suggestions in improving the plugins. In addition, we also thank the image.sc community for technical assistance and advice.

# Installation and Tutorials

To download SIAL, open FIJI, go to “Help > Update…” and then update FIJI. After FIJI is finished downloading all updates, a window named “ImageJ Updater” will open. Select “Manage Update Sites > Add update site” and add this url: https://sites.imagej.net/D-tear/

Be sure to check the box next to this update site to ensure the FIJI adds SIAL to your FIJI Plugins folder. Select “Close > Apply changes”. FIJI will download SIAL.jar and associated dependencies. After successfully updating, FIJI will then ask to be closed and restarted. After doing this, SIAL can be accessed via the Plugins tab in FIJI. Note that SIAL will usually be installed towards the bottom of the available FIJI plugins.

Links to YouTube tutorials covering the installation and use of SIAL:

Installing SIAL: https://youtu.be/RU4B3GhVwaM

How to use the File Randomizer program inside of SIAL: https://youtu.be/_oQLJ5gC7ls

How to use the PhenoScoreKeeper inside of SIAL: https://youtu.be/FiaNubIDvSw

How to use the ROI Recorder program inside of SIAL: https://youtu.be/9mTHGVeWJA0
