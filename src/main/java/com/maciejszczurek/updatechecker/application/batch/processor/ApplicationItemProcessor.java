package com.maciejszczurek.updatechecker.application.batch.processor;

import com.maciejszczurek.updatechecker.application.model.Application;
import com.maciejszczurek.updatechecker.application.model.ApplicationType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.springframework.batch.item.ItemProcessor;

@NoArgsConstructor
@Log4j2
public class ApplicationItemProcessor
  implements ItemProcessor<Application, Application> {

  private static final Map<String, ApplicationType> APPLICATION_TYPE_LIST;

  static {
    APPLICATION_TYPE_LIST = new HashMap<>();

    APPLICATION_TYPE_LIST.put(
      "dobreprogramy.pl",
      ApplicationType.DOBREPROGRAMY
    );
    APPLICATION_TYPE_LIST.put(
      "download.osmand.net",
      ApplicationType.OSMAND_MAPS
    );
    //    APPLICATION_TYPE_LIST.put("repo.spring.io", MAVEN_METADATA);
    APPLICATION_TYPE_LIST.put("mvnrepository.com", ApplicationType.MVN_REPO);
    APPLICATION_TYPE_LIST.put(
      "getcomposer.org/download",
      ApplicationType.COMPOSER
    );
    APPLICATION_TYPE_LIST.put("npmjs.com", ApplicationType.NPM_REPO);
    APPLICATION_TYPE_LIST.put(
      "docs.spring.io",
      ApplicationType.HTTP_APACHE_LIST_LAST_MODIFIED
    );
    APPLICATION_TYPE_LIST.put(
      "packagist.org/packages",
      ApplicationType.PACKAGIST
    );
    //    APPLICATION_TYPE_LIST.put("apps.ankiweb.net", ANKI);
    APPLICATION_TYPE_LIST.put(
      "singularlabs.com/software/ccenhancer/download-ccenhancer",
      ApplicationType.CCENHANCER
    );
    APPLICATION_TYPE_LIST.put(
      "content.thewebatom.net/files/winapp2.ini",
      ApplicationType.WINAPP2
    );
    APPLICATION_TYPE_LIST.put(
      "piriform.com/ccleaner/download",
      ApplicationType.CCLEANER
    );
    //    APPLICATION_TYPE_LIST.put("crystalmark.info", CRYSTAL_DISK_INFO);
    APPLICATION_TYPE_LIST.put("cygwin.com/index.html", ApplicationType.CYGWIN);
    APPLICATION_TYPE_LIST.put(
      "ftp-stud.hs-esslingen.de",
      ApplicationType.CYGWIN_SETUP
    );
    APPLICATION_TYPE_LIST.put(
      "driver-soft.com/download.html",
      ApplicationType.DRIVER_GENIUS
    );
    //    APPLICATION_TYPE_LIST.put("github.com/OpenVPN/easy-rsa/releases", EASY_RSA);
    //    APPLICATION_TYPE_LIST.put("waterfoxproject.org", WATERFOX);
    APPLICATION_TYPE_LIST.put(
      "git-scm.com/download/win",
      ApplicationType.GIT_SCM
    );
    APPLICATION_TYPE_LIST.put("gpg4win.org", ApplicationType.GPG4WIN);
    //    APPLICATION_TYPE_LIST.put("gradle.org/install", GRADLE);
    APPLICATION_TYPE_LIST.put(
      "data.services.jetbrains.com/products",
      ApplicationType.JETBRAINS
    );
    APPLICATION_TYPE_LIST.put(
      "gluonhq.com/products/scene-builder",
      ApplicationType.JAVA_SCENE_BUILDER
    );
    APPLICATION_TYPE_LIST.put("josm.openstreetmap.de", ApplicationType.JOSM);
    APPLICATION_TYPE_LIST.put("macpass.github.io", ApplicationType.MAC_PASS);
    APPLICATION_TYPE_LIST.put(
      "navicat.com/en/products/navicat-premium-quit-note",
      ApplicationType.NAVICAT
    );
    APPLICATION_TYPE_LIST.put(
      "nodejs.org/en/download/current",
      ApplicationType.NODEJS
    );
    APPLICATION_TYPE_LIST.put(
      "notepad-plus-plus.org",
      ApplicationType.NOTEPAD_PP
    );
    APPLICATION_TYPE_LIST.put(
      "regexbuddy.com/history.html",
      ApplicationType.REGEX_BUDDY
    );
    //    APPLICATION_TYPE_LIST.put("ericzhang.me/projects", EZ_BLOCKER);
    APPLICATION_TYPE_LIST.put("tcup.pl", ApplicationType.TC_UP);
    APPLICATION_TYPE_LIST.put(
      "tortoisegit.org/download",
      ApplicationType.TORTOISE_GIT
    );
    APPLICATION_TYPE_LIST.put(
      "tweaking.com/content/page/windows_repair_all_in_one.html",
      ApplicationType.WINDOWS_REPAIR
    );
    APPLICATION_TYPE_LIST.put("videolan.org", ApplicationType.VLC);
    APPLICATION_TYPE_LIST.put(
      "navigation.com",
      ApplicationType.NAVIGATION_MAPS
    );
    APPLICATION_TYPE_LIST.put("filehippo.com/pl", ApplicationType.FILE_HIPPO);
    APPLICATION_TYPE_LIST.put(
      "xnview.com/en/xnview.php",
      ApplicationType.XN_VIEW
    );
    APPLICATION_TYPE_LIST.put(
      "station-drivers.com",
      ApplicationType.STATION_DRIVERS
    );
    APPLICATION_TYPE_LIST.put("support.amd.com", ApplicationType.AMD_DRIVERS);
    APPLICATION_TYPE_LIST.put(
      "218.210.127.131",
      ApplicationType.REALTEK_ETHERNET_WINDOWS10
    );
  }

  private static void processStationDrivers(
    @NotNull final Application application
  ) throws IOException {
    final String siteUrl = application.getSiteUrl();

    if (siteUrl.startsWith("https://")) {
      application.setSiteUrl(siteUrl.replace("https://", "http://"));
    }

    application.setName(
      Jsoup
        .connect(application.getSiteUrl())
        .get()
        .select("#remositoryfilelisting > div:nth-child(1) > h3 > a")
        .text()
        .replace("Version ", "")
        .replace(" WHQL", "")
        .replace(" " + application.getCurrentVersion(), "")
    );
  }

  @Override
  public Application process(@NotNull final Application item)
    throws IOException {
    final String siteUrl = item.getSiteUrl();

    for (final Entry<String, ApplicationType> entry : APPLICATION_TYPE_LIST.entrySet()) {
      if (siteUrl.contains(entry.getKey())) {
        item.setApplicationType(entry.getValue());

        if (item.getApplicationType() == ApplicationType.STATION_DRIVERS) {
          processStationDrivers(item);
        }

        return item;
      }
    }

    log.info("{} rejected.", item.getName());

    return null;
  }
}
