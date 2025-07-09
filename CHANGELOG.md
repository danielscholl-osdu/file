# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.0.0 (2025-07-09)


### ✨ Features

* Using keda scaling ([28d237a](https://github.com/danielscholl-osdu/file/commit/28d237a59b702df39a7f2d2f4a5c3289ee5acd8b))


### 🐛 Bug Fixes

* Aws issue generating download urls of 15 minute durations ([c527f7d](https://github.com/danielscholl-osdu/file/commit/c527f7d06449f681f1d03e33c0b89d96e5c8e227))
* Aws issue generating download urls of 15 minute durations ([565f80b](https://github.com/danielscholl-osdu/file/commit/565f80b9d8d8f0a7cba6b9ea71f06602d9228290))
* Cve fix for jackson-dataformat ([d91c72d](https://github.com/danielscholl-osdu/file/commit/d91c72db39caefb0d3fd24422e7c11ed69c6b550))
* Cve fix for jackson-dataformat ([f50e02d](https://github.com/danielscholl-osdu/file/commit/f50e02da02211c18c1edb336268a186116e7d13d))
* Duplicate PreloadFilePath value ([561969a](https://github.com/danielscholl-osdu/file/commit/561969ac574d1c0a440cde4dc1da6bbe12986b00))
* Error handling for invalid file path converts 400 to 500 by global exception handler ([dd30689](https://github.com/danielscholl-osdu/file/commit/dd3068941324572cf6fc8f25d2647e8ea8f44a14))
* Error handling for invlaid file path converts 400 to 500 by global exception handler ([8dc7dc2](https://github.com/danielscholl-osdu/file/commit/8dc7dc216f8a50b2b1d0be2336b3e6305d6ec597))
* Gc chart: add default SA name ([fe88346](https://github.com/danielscholl-osdu/file/commit/fe88346aa6009d262ad0f56190d0ac4827cb5df8))
* Gc chart: add default SA name ([929440c](https://github.com/danielscholl-osdu/file/commit/929440cf5b4ec96d51a59df35bc03e368379ea06))
* SIGNED_URL_EXPIRY_TIME_MINUTES Override, Local Acceptance Test Build and README ([9b28f55](https://github.com/danielscholl-osdu/file/commit/9b28f553099fe637d743ea57adb01cc97d788108))
* SIGNED_URL_EXPIRY_TIME_MINUTES Override, Local Acceptance Test Build and README ([4c25c0e](https://github.com/danielscholl-osdu/file/commit/4c25c0e668e16e0e74b26f3744f582c8442de23e))
* Spring boot netty handler c-ares version bump ([e5b63da](https://github.com/danielscholl-osdu/file/commit/e5b63da37549112e61c076fb27dc868fd97c4fe1))
* Spring boot netty handler c-ares version bump ([dfa1f54](https://github.com/danielscholl-osdu/file/commit/dfa1f547431ac915fa38667784b02846e337fc7d))
* Spring security update ([2638f87](https://github.com/danielscholl-osdu/file/commit/2638f8789d0765eaadc8be7ac7d458bb3c27e567))
* Spring security update ([258a4d8](https://github.com/danielscholl-osdu/file/commit/258a4d84bde8a831ab620d32fdf4043bcdc40d2c))
* Throws 400 error when getFileList request filter returns no results ([3aedbc8](https://github.com/danielscholl-osdu/file/commit/3aedbc8f9c44c6df15cbb3f74c40ded2ebd17b31))
* Throws 400 error when getFileList request filter returns no results ([664dac7](https://github.com/danielscholl-osdu/file/commit/664dac7a1f68eae66ffe0494e3251b3f2b00b9ff))
* Tomcat cve ([fc81fcb](https://github.com/danielscholl-osdu/file/commit/fc81fcb0b0c0335f3cd5db5230020fdec10e6c6d))
* Validation of max key length using S3 on AWS ([70eedd7](https://github.com/danielscholl-osdu/file/commit/70eedd73db7bb1e39a0c504f1b4ffdfde7704ce7))
* Validation of max key length using S3 on AWS ([409ea11](https://github.com/danielscholl-osdu/file/commit/409ea1195307417720a2feebfb6564866cab1261))


### 📚 Documentation

* Updating helm chart documentation and versioning ([c5a461e](https://github.com/danielscholl-osdu/file/commit/c5a461ee2b32bd3cf122e4625e2c489eb3f0488f))


### 🔧 Miscellaneous

* Complete repository initialization ([66b54b3](https://github.com/danielscholl-osdu/file/commit/66b54b37c04b7ded443fe7303cb93a1a587298c3))
* Copy configuration and workflows from main branch ([f2ea22b](https://github.com/danielscholl-osdu/file/commit/f2ea22b90cb2cdbec44ea37bda81f5582c7280f9))
* Deleting aws helm chart ([4ad1dc9](https://github.com/danielscholl-osdu/file/commit/4ad1dc94ca9bb59f6d49b21a29d7fba0ca64111b))
* Deleting aws helm chart ([47c1a0a](https://github.com/danielscholl-osdu/file/commit/47c1a0a7be5375ba9244d0d7a75aa95546413b03))
* Removing helm copy from aws buildspec ([688d1bd](https://github.com/danielscholl-osdu/file/commit/688d1bd2027072fcc0a87f109ade672c12733647))

## [2.0.0] - Major Workflow Enhancement & Documentation Release

### ✨ Features
- **Comprehensive MkDocs Documentation Site**: Complete documentation overhaul with GitHub Pages deployment
- **Automated Cascade Failure Recovery**: System automatically recovers from cascade workflow failures
- **Human-Centric Cascade Pattern**: Issue lifecycle tracking with human notifications for critical decisions
- **Integration Validation**: Comprehensive validation system for cascade workflows
- **Claude Workflow Integration**: Full Claude Code CLI support with Maven MCP server integration
- **GitHub Copilot Enhancement**: Java development environment setup and firewall configuration
- **Fork Resources Staging Pattern**: Template-based staging for fork-specific configurations
- **Conventional Commits Validation**: Complete validation system with all supported commit types
- **Enhanced PR Label Management**: Simplified production PR labels with automated issue closure
- **Meta Commit Strategy**: Advanced release-please integration for better version management
- **Push Protection Handling**: Sophisticated upstream secrets detection and resolution workflows

### 🔨 Build System
- **Workflow Separation Pattern**: Template development vs. fork instance workflow isolation
- **Template Workflow Management**: 9 comprehensive template workflows for fork management
- **Enhanced Action Reliability**: Improved cascade workflow trigger reliability with PR event filtering
- **Base64 Support**: Enhanced create-enhanced-pr action with encoding capabilities

### 📚 Documentation
- **Structured MkDocs Site**: Complete documentation architecture with GitHub Pages
- **AI-First Development Docs**: Comprehensive guides for AI-enhanced development
- **ADR Documentation**: 20+ Architectural Decision Records covering all major decisions
- **Workflow Specifications**: Detailed documentation for all 9 template workflows
- **Streamlined README**: Focused quick-start guide directing to comprehensive documentation

### 🛡️ Security & Reliability
- **Advanced Push Protection**: Intelligent handling of upstream repositories with secrets
- **Branch Protection Integration**: Automated branch protection rule management
- **Security Pattern Recognition**: Enhanced security scanning and pattern detection
- **MCP Configuration**: Secure Model Context Protocol integration for AI development

### 🔧 Workflow Enhancements
- **Cascade Monitoring**: Advanced cascade workflow monitoring and SLA management
- **Dependabot Integration**: Enhanced dependabot validation and automation
- **Template Synchronization**: Sophisticated template update propagation system
- **Issue State Tracking**: Advanced issue lifecycle management and tracking
- **GITHUB_TOKEN Standardization**: Improved token handling across all workflows

### ♻️ Code Refactoring
- **Removed AI_EVOLUTION.md**: Migrated to structured ADR approach for better maintainability
- **Simplified README Structure**: Eliminated redundancy between README and documentation site
- **Enhanced Initialization Cleanup**: Improved fork repository cleanup and setup process
- **Standardized Error Handling**: Consistent error handling patterns across all workflows

### 🐛 Bug Fixes
- **YAML Syntax Issues**: Resolved multiline string handling in workflow configurations
- **Release Workflow Compatibility**: Updated to googleapis/release-please-action@v4
- **MCP Server Configuration**: Fixed Maven MCP server connection and configuration issues
- **Cascade Trigger Reliability**: Implemented pull_request_target pattern for better triggering
- **Git Diff Syntax**: Corrected git command syntax in sync-template workflow
- **Label Management**: Standardized label usage across all workflows and templates

## [1.0.0] - Initial Release

### ✨ Features
- Initial release of OSDU Fork Management Template
- Automated fork initialization workflow
- Daily upstream synchronization with AI-enhanced PR descriptions
- Three-branch management strategy (main, fork_upstream, fork_integration)
- Automated conflict detection and resolution guidance
- Semantic versioning and release management
- Template development workflows separation

### 📚 Documentation
- Complete architectural decision records (ADRs)
- Product requirements documentation
- Development and usage guides
- GitHub Actions workflow documentation
