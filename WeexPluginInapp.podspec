# coding: utf-8

Pod::Spec.new do |s|
  s.name         = "WeexPluginInapp"
  s.version      = "0.3.1"
  s.summary      = "Weex Plugin for InApp Purchases in iOS and Android"

  s.description  = <<-DESC
                   Weexplugin Source Description
                   DESC

  s.homepage     = "https://github.com"
  s.license = {
    :type => 'Copyright',
    :text => <<-LICENSE
            copyright
    LICENSE
  }
  s.authors      = {
                     "DSeeKer" =>"siqueira@eyzmedia.de"
                   }
  s.platform     = :ios
  s.ios.deployment_target = "9.0"

  s.source       = { :git => 'https://github.com/eyzhub/weex-plugin-inapp.git', :tag => '0.3.1' }
  s.source_files  = "ios/Sources/*.{h,m,mm}"
  
  s.static_framework = true
  s.requires_arc = true
  s.dependency "WeexPluginLoader"
  s.dependency "WeexSDK"
  s.dependency "RMStore"
  s.dependency "RMStore/NSUserDefaultsPersistence"
end
