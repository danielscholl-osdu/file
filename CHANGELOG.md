# Changelog

## [1.2.0](https://github.com/danielscholl-osdu/file/compare/v1.1.0...v1.2.0) (2026-09-26)


### ✨ Features

* Add vendor-neutral OIDC authentication support for acceptance tests ([ef21e9e](https://github.com/danielscholl-osdu/file/commit/ef21e9e6df900b9530746a9119cda991f9daa699))
* Add vendor-neutral OIDC authentication support for acceptance tests ([702723a](https://github.com/danielscholl-osdu/file/commit/702723a075c743a9cb802427d8156cd0ff438592))


### 🐛 Bug Fixes

* AWS acceptance single-prefix ACL (201/200) + presigned-URL download 403 ([8c702c1](https://github.com/danielscholl-osdu/file/commit/8c702c19efe018f4e6e8688e0e6e038cf4052f2b))
* AWS acceptance single-prefix ACL (201/200) + presigned-URL download 403 ([8684b2d](https://github.com/danielscholl-osdu/file/commit/8684b2db69b3ab254fbac45e320d350e7af67fa3))
* Aws issue generating download urls of 15 minute durations ([c527f7d](https://github.com/danielscholl-osdu/file/commit/c527f7d06449f681f1d03e33c0b89d96e5c8e227))
* Aws issue generating download urls of 15 minute durations ([565f80b](https://github.com/danielscholl-osdu/file/commit/565f80b9d8d8f0a7cba6b9ea71f06602d9228290))
* **aws:** Bump c-ares pin to 1.34.8-r0 to fix file-aws image build ([b473d43](https://github.com/danielscholl-osdu/file/commit/b473d43ce6df5cc1d01b1529d250b26549b68824))
* **aws:** Bump c-ares pin to 1.34.8-r0 to fix file-aws image build ([ab6232d](https://github.com/danielscholl-osdu/file/commit/ab6232d97782d7bf7cde920aceaf31f5dbd313c2))
* **azure:** Netty-bom before core-lib-azure (lettuce 7.5.2 NoClassDefFoundError) ([74631f0](https://github.com/danielscholl-osdu/file/commit/74631f00229ceac3ae0d9a4da2565e1f6cd15e0a))
* **azure:** Netty-bom before core-lib-azure (lettuce 7.5.2 NoClassDefFoundError) ([928a7be](https://github.com/danielscholl-osdu/file/commit/928a7becbb82675b5ffc97142be14331c80b5194))
* Cve and spring boot version bump ([b2a09e0](https://github.com/danielscholl-osdu/file/commit/b2a09e0ce93dbfa42fb538dbb3476919cd6fc992))
* Cve and spring boot version bump ([30e713c](https://github.com/danielscholl-osdu/file/commit/30e713c10a9311e722383a0f9d84cd0ddf9fd994))
* Cve fix for jackson-dataformat ([d91c72d](https://github.com/danielscholl-osdu/file/commit/d91c72db39caefb0d3fd24422e7c11ed69c6b550))
* Cve fix for jackson-dataformat ([f50e02d](https://github.com/danielscholl-osdu/file/commit/f50e02da02211c18c1edb336268a186116e7d13d))
* **cve:** Remediate HIGH/CRITICAL deps + pom cleanup ([ed11f43](https://github.com/danielscholl-osdu/file/commit/ed11f433b7c793df64d0138eda6a3aa130b1ff31))
* **cve:** Remediate HIGH/CRITICAL deps + pom cleanup ([4fdafc2](https://github.com/danielscholl-osdu/file/commit/4fdafc21f00a0ed114dfebdb5d097135151bdf91))
* **docs:** Correct S3 config markdown anchor in baremetal README ([5366ea6](https://github.com/danielscholl-osdu/file/commit/5366ea6bc1149aa72b51b480c50da7eb857c9e5b))
* Duplicate PreloadFilePath value ([561969a](https://github.com/danielscholl-osdu/file/commit/561969ac574d1c0a440cde4dc1da6bbe12986b00))
* Error handling for invalid file path converts 400 to 500 by global exception handler ([dd30689](https://github.com/danielscholl-osdu/file/commit/dd3068941324572cf6fc8f25d2647e8ea8f44a14))
* Error handling for invlaid file path converts 400 to 500 by global exception handler ([8dc7dc2](https://github.com/danielscholl-osdu/file/commit/8dc7dc216f8a50b2b1d0be2336b3e6305d6ec597))
* **file-azure:** Bump core-lib-azure to 3.0.3 for single-scope workload identity tokens ([e24fb82](https://github.com/danielscholl-osdu/file/commit/e24fb824f63511b187282ca9d799d2f4516e4a28))
* Gc chart: add default SA name ([fe88346](https://github.com/danielscholl-osdu/file/commit/fe88346aa6009d262ad0f56190d0ac4827cb5df8))
* Gc chart: add default SA name ([929440c](https://github.com/danielscholl-osdu/file/commit/929440cf5b4ec96d51a59df35bc03e368379ea06))
* SIGNED_URL_EXPIRY_TIME_MINUTES Override, Local Acceptance Test Build and README ([9b28f55](https://github.com/danielscholl-osdu/file/commit/9b28f553099fe637d743ea57adb01cc97d788108))
* SIGNED_URL_EXPIRY_TIME_MINUTES Override, Local Acceptance Test Build and README ([4c25c0e](https://github.com/danielscholl-osdu/file/commit/4c25c0e668e16e0e74b26f3744f582c8442de23e))
* Spring boot netty handler c-ares version bump ([e5b63da](https://github.com/danielscholl-osdu/file/commit/e5b63da37549112e61c076fb27dc868fd97c4fe1))
* Spring boot netty handler c-ares version bump ([dfa1f54](https://github.com/danielscholl-osdu/file/commit/dfa1f547431ac915fa38667784b02846e337fc7d))
* Spring security update ([2638f87](https://github.com/danielscholl-osdu/file/commit/2638f8789d0765eaadc8be7ac7d458bb3c27e567))
* Spring security update ([258a4d8](https://github.com/danielscholl-osdu/file/commit/258a4d84bde8a831ab620d32fdf4043bcdc40d2c))
* Spring-core tomcat netty version bump ([ed6be42](https://github.com/danielscholl-osdu/file/commit/ed6be428bbd7d3481bb472037a0c40b130d89011))
* Spring-core tomcat netty version bump ([9d852dc](https://github.com/danielscholl-osdu/file/commit/9d852dc404f4dc276d82cf2c0cd1562a877bbad0))
* Throws 400 error when getFileList request filter returns no results ([3aedbc8](https://github.com/danielscholl-osdu/file/commit/3aedbc8f9c44c6df15cbb3f74c40ded2ebd17b31))
* Throws 400 error when getFileList request filter returns no results ([664dac7](https://github.com/danielscholl-osdu/file/commit/664dac7a1f68eae66ffe0494e3251b3f2b00b9ff))
* Tomcat cve ([fc81fcb](https://github.com/danielscholl-osdu/file/commit/fc81fcb0b0c0335f3cd5db5230020fdec10e6c6d))
* Validation of max key length using S3 on AWS ([70eedd7](https://github.com/danielscholl-osdu/file/commit/70eedd73db7bb1e39a0c504f1b4ffdfde7704ce7))
* Validation of max key length using S3 on AWS ([409ea11](https://github.com/danielscholl-osdu/file/commit/409ea1195307417720a2feebfb6564866cab1261))


### 📚 Documentation

* Add CONTRIBUTING guide and license headers ([d1160d0](https://github.com/danielscholl-osdu/file/commit/d1160d040b885b73ff3a91f3fa1d31c93ad17ce4))
* Add CONTRIBUTING guide and license headers ([3809151](https://github.com/danielscholl-osdu/file/commit/38091512c756f5c1daf22c8d50b7a8303a7c00c1))


### 🔧 Miscellaneous

* Add service descriptor ([3cd4514](https://github.com/danielscholl-osdu/file/commit/3cd4514b1f745a6eef1467a2133e0516f112bf7c))
* Add spi service descriptor for file service ([c251368](https://github.com/danielscholl-osdu/file/commit/c25136864c5014fa56394154e131d2b7ffbffa46))
* Add upstream filter config for file service ([36c2dda](https://github.com/danielscholl-osdu/file/commit/36c2ddabfe7846cf9e6dff2a26c9466b166b81d5))
* **ci:** Remove IBM jobs from pipeline ([5f24fd1](https://github.com/danielscholl-osdu/file/commit/5f24fd1ce327e233abe98a238aeede39683815d2))
* **ci:** Remove IBM jobs from pipeline ([ae8856c](https://github.com/danielscholl-osdu/file/commit/ae8856cb088cccb8444af374a7aaca4b74ab29b7))
* Complete repository initialization ([68fd1f6](https://github.com/danielscholl-osdu/file/commit/68fd1f6aa461c7bbee55d58a0c810557d67d74a5))
* Copy configuration and workflows from main branch ([57372b1](https://github.com/danielscholl-osdu/file/commit/57372b1a824694983d39bcc344a1f5c33c2962d8))
* Deleting aws helm chart ([4ad1dc9](https://github.com/danielscholl-osdu/file/commit/4ad1dc94ca9bb59f6d49b21a29d7fba0ca64111b))
* Deleting aws helm chart ([47c1a0a](https://github.com/danielscholl-osdu/file/commit/47c1a0a7be5375ba9244d0d7a75aa95546413b03))
* **deps:** Security remediation - Spring Boot 3.5.8, Spring Cloud 2025.0.0 ([dda0707](https://github.com/danielscholl-osdu/file/commit/dda07070269e5ff83107de2fed98c9dd629c5f69))
* **deps:** Security remediation - Spring Boot 3.5.8, Spring Cloud 2025.0.0 ([6fa1324](https://github.com/danielscholl-osdu/file/commit/6fa13247db10941d50d3121d29efb35fdb6c74a0))
* **deps:** Update commons-fileupload to 1.6.0 ([fd60cbf](https://github.com/danielscholl-osdu/file/commit/fd60cbf52fe2624c1eaa515c58cb39f2b58e5d22))
* **deps:** Update commons-fileupload to 1.6.0 ([e93dc49](https://github.com/danielscholl-osdu/file/commit/e93dc494e4c733839f55933471112b78cb3ec6d8))
* Generate filtered upstream tree ([647842f](https://github.com/danielscholl-osdu/file/commit/647842fbaff85a0d1966a2225dffe7e897f991bb))
* Remove AWS provider and AWS CI/CD ([af461e8](https://github.com/danielscholl-osdu/file/commit/af461e8c8aa55245f289118f13029757ed6ac8ee))
* Remove AWS provider and AWS CI/CD ([0602220](https://github.com/danielscholl-osdu/file/commit/060222019ef54ac0841ee787fbe78e0c51588a50))
* Removing helm copy from aws buildspec ([688d1bd](https://github.com/danielscholl-osdu/file/commit/688d1bd2027072fcc0a87f109ade672c12733647))
* Seed fork-owned azure trees ([7d10769](https://github.com/danielscholl-osdu/file/commit/7d10769ed033a8a7ec4e451631e0a99de079d223))
* Sync template updates ([327cdf7](https://github.com/danielscholl-osdu/file/commit/327cdf73799475d06036f1d469e565aa15e2dcdb))
* **template-sync:** Sync template updates (updated 2026-09-26) ([aa70cfa](https://github.com/danielscholl-osdu/file/commit/aa70cfa57cfbedc23d192a6e255c625b2a397eaa))


### ♻️ Code Refactoring

* **audit:** Encapsulate audit roles in logging layer ([2b149c5](https://github.com/danielscholl-osdu/file/commit/2b149c5ec48451034fbc2562c7cbe622f41843d1))
* **audit:** Encapsulate audit roles in logging layer ([4efe561](https://github.com/danielscholl-osdu/file/commit/4efe5611720a0b6f5e75c8b60587448890d6d3c4))
* **logging:** Align file metadata and OSM logging with JaxRsDpsLog ([84db264](https://github.com/danielscholl-osdu/file/commit/84db26464f0c7ef629b3b4785a802fb88241e4c1))


### 🧪 Tests

* Validate File Collection S3 signing credentials ([53ef4b0](https://github.com/danielscholl-osdu/file/commit/53ef4b0a94c287df6d4fe022b0daa66e0f2056e1))
* Validate File Collection S3 signing credentials ([843d6d8](https://github.com/danielscholl-osdu/file/commit/843d6d89c69c751fd2afbb04df06a043a6b1c1f8))


### 🔨 Build System

* **deps:** Bump core-lib-azure to 3.0.3 in file-azure ([9cc2968](https://github.com/danielscholl-osdu/file/commit/9cc296801d428eda066361dccd49557e48fa8641))
* Remove redundant version declarations from child modules ([631a10a](https://github.com/danielscholl-osdu/file/commit/631a10a44e1d854a36fb81900da9d0b3a5423e73))
* Remove redundant version declarations from child modules ([86651bf](https://github.com/danielscholl-osdu/file/commit/86651bf6d9a2f31d6cd11863bef9d1ec55c8f0f4))
