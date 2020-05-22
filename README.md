# SIAL
### Simple Image Analysis Library

SIAL: A simple image analysis library for wet-lab scientists

David R. Tyrpak 1, 
Yaocun Li 1, 
Siqi Lei 1, 
J.A. MacKay1,2,3

1Department of Pharmacology and Pharmaceutical Sciences, School of Pharmacy of the University of Southern California, 1985 Zonal Ave, Los Angeles, CA, USA 90089 

2Keck School of Medicine of the University of Southern California, Department of Ophthalmology, Roski Eye Institute. 1450 San Pablo St, Los Angeles, CA, USA 90033

3University of Southern California Viterbi School of Engineering, Biomedical Engineering. 1042 Downey Way, Los Angeles, CA, USA 90089

### Summary:

In many biomedical research labs, image analysis tasks are relatively simple but labor intensive. For example, a typical workflow may require human intervention to outline regions of interest or score phenotypes from a collection of images. Such tasks could potentially be partially automated with a script, but in our experience, many biomedical researchers do not obtain programming skills. Furthermore, image analysis is typically just one of the many experiments that a busy researcher will employ during the course of a project, and so devoting time to learning programming or bespoke image analysis is not prioritized. These factors create an unfortunate situation where, in addition to being labor intensive, image analysis workflows may become biased and exhibit limited reproducibility. 

As primarily “wet-lab” scientists, we wanted to develop user friendly plugins focused on the most common tasks that fellow wet-lab researchers encounter during image analysis.  Because FIJI is routinely used by biomedical researchers the world over, we developed a series of FIJI plugins written in java.  We name this package SIAL: a simple image analysis library. It aids users in human-assisted image analysis by providing plugins for image randomization, phenotype scoring, and ROI selection. In addition, because wet-lab scientists are typically interrupted by multiple experiments, SIAL keeps track of which images in a directory have already been analyzed so that researchers can easily start and stop their workflows without hassle.

SIAL is easily installed via the ImageJ update website service, utilizes simple user interfaces, requires no programming experience, and requires no dependencies except FIJI. The individual plugins can be easily integrated with workflows involving other FIJI plugins or with workflows employing another open source software, like Cell Profiler or QuPath. 

### Author contributions:
SIAL was developed by DRT at the MacKay lab in the School of Pharmacy of the University of Southern California. YL and SL tested the software and reported bugs.

### Acknowledgements:
This work was supported by grant F31DK118881 to DRT. We thank Anh Truong for valuable suggestions in improving the plugins. In addition, we also thank the image.sc community for technical assistance and advice.

## Installation
To download SIAL, open FIJI, go to “Help > Update…” and then update FIJI. After FIJI is finished downloading all updates, a window named “ImageJ Updater” will open. Select “Manage Update Sites > Add update site” and add this url: https://sites.imagej.net/D-tear/

Be sure to check the box next to this update site to ensure the FIJI adds SIAL to your FIJI Plugins folder. Select “Close > Apply changes”. FIJI will download SIAL.jar and associated dependencies. After successfully updating, FIJI will then ask to be closed and restarted. After doing this, SIAL can be accessed via the Plugins tab in FIJI. Note that SIAL will usually be installed towards the bottom of the available FIJI plugins. 
