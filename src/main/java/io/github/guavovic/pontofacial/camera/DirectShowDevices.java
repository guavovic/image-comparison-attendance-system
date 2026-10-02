package io.github.guavovic.pontofacial.camera;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class DirectShowDevices {

    private static final String SCRIPT = """
            [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
            Add-Type -TypeDefinition @'
            using System;
            using System.Collections.Generic;
            using System.Runtime.InteropServices;
            using System.Runtime.InteropServices.ComTypes;

            public static class DsList {
                [ComImport, Guid("29840822-5B84-11D0-BD3B-00A0C911CE86"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
                interface ICreateDevEnum { [PreserveSig] int CreateClassEnumerator([In] ref Guid type, out IEnumMoniker enumerator, int flags); }
                [ComImport, Guid("55272A00-42CB-11CE-8135-00AA004BB851"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
                interface IPropertyBag { [PreserveSig] int Read([MarshalAs(UnmanagedType.LPWStr)] string name, out object value, IntPtr log); }

                public static List<string> Names() {
                    var result = new List<string>();
                    var clsid = new Guid("62BE5D10-60EB-11D0-BD3B-00A0C911CE86");
                    var dev = (ICreateDevEnum)Activator.CreateInstance(Type.GetTypeFromCLSID(clsid));
                    var category = new Guid("860BB310-5D01-11D0-BD3B-00A0C911CE86");
                    IEnumMoniker enumerator;
                    if (dev.CreateClassEnumerator(ref category, out enumerator, 0) != 0 || enumerator == null) return result;
                    var monikers = new IMoniker[1];
                    while (enumerator.Next(1, monikers, IntPtr.Zero) == 0) {
                        object bag; var iid = typeof(IPropertyBag).GUID;
                        monikers[0].BindToStorage(null, null, ref iid, out bag);
                        object name; ((IPropertyBag)bag).Read("FriendlyName", out name, IntPtr.Zero);
                        result.Add((string)name);
                        Marshal.ReleaseComObject(monikers[0]);
                    }
                    return result;
                }
            }
            '@
            $i = 0
            [DsList]::Names() | ForEach-Object { "$i`t$_"; $i++ }
            """;

    private DirectShowDevices() {
    }

    public static List<Camera.Info> list() {
        String encoded = Base64.getEncoder().encodeToString(SCRIPT.getBytes(StandardCharsets.UTF_16LE));
        ProcessBuilder builder = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive",
                "-EncodedCommand", encoded);
        builder.redirectError(ProcessBuilder.Redirect.DISCARD);
        try {
            Process process = builder.start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (!process.waitFor(20, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new CameraException("A lista de câmeras demorou demais.");
            }
            if (process.exitValue() != 0) {
                throw new CameraException("Não foi possível listar as câmeras.");
            }
            return parse(output);
        } catch (IOException e) {
            throw new CameraException("Não foi possível listar as câmeras.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CameraException("A lista de câmeras foi interrompida.", e);
        }
    }

    static List<Camera.Info> parse(String output) {
        List<Camera.Info> cameras = new ArrayList<>();
        for (String line : output.split("\\R")) {
            int tab = line.indexOf('\t');
            if (tab <= 0) {
                continue;
            }
            try {
                cameras.add(new Camera.Info(Integer.parseInt(line.substring(0, tab).strip()),
                        line.substring(tab + 1).strip()));
            } catch (NumberFormatException e) {
                // Linha que não é de dispositivo (aviso do PowerShell, por exemplo).
            }
        }
        return cameras;
    }

    public static boolean isVirtual(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.contains("virtual") || lower.contains("quest");
    }
}
