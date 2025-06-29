package com.example.autism_221351038.ui.simulation

import android.content.res.AssetFileDescriptor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.autism_221351038.R
import com.google.android.material.textfield.TextInputEditText
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulationFragment : Fragment() {
    private lateinit var interpreter: Interpreter
    private val mModelPath = "autism-screening-on-adults.tflite"


    private lateinit var btnCheck: Button
    private lateinit var inputAge: TextInputEditText
    private lateinit var genderSpinner: AutoCompleteTextView
    private lateinit var ethnicitySpinner: AutoCompleteTextView
    private lateinit var jaundiceSpinner: AutoCompleteTextView
    private lateinit var familyAsdSpinner: AutoCompleteTextView
    private lateinit var countrySpinner: AutoCompleteTextView
    private lateinit var usedAppSpinner: AutoCompleteTextView
    private lateinit var relationSpinner: AutoCompleteTextView
    private val questionRadioGroups = mutableListOf<RadioGroup>()


    private val genderOptions = arrayOf("f", "m")
    private val ethnicityOptions = arrayOf(
        "White-European", "Asian", "Middle Eastern ", "Black", "South Asian",
        "Others", "Latino", "Hispanic", "Pasifika", "Turkish", "others"
    )
    private val yesNoOptions = arrayOf("no", "yes")
    private val countryOptions = arrayOf(
        "United States", "United Arab Emirates", "New Zealand", "India",
        "United Kingdom", "Australia", "Malaysia", "Canada", "Sri Lanka",
        "Afghanistan", "Jordan", "Oman", "Pakistan", "Iran", "others"
    )
    private val relationOptions = arrayOf("Self", "Parent", "Relative", "Health care professional", "Others")



    private val expectedFeatures = listOf(
        "A1_Score", "A2_Score", "A3_Score", "A4_Score", "A5_Score", "A6_Score", "A7_Score", "A8_Score", "A9_Score", "A10_Score",
        "age", "result", "gender_m", "ethnicity_Asian", "ethnicity_Black", "ethnicity_Hispanic", "ethnicity_Latino", "ethnicity_Middle Eastern",
        "ethnicity_Others", "ethnicity_Pasifika", "ethnicity_South Asian", "ethnicity_Turkish", "ethnicity_White-European", "ethnicity_others",
        "jundice_yes", "austim_yes", "contry_of_res_AmericanSamoa", "contry_of_res_Angola", "contry_of_res_Argentina", "contry_of_res_Armenia",
        "contry_of_res_Aruba", "contry_of_res_Australia", "contry_of_res_Austria", "contry_of_res_Azerbaijan", "contry_of_res_Bahamas",
        "contry_of_res_Bangladesh", "contry_of_res_Belgium", "contry_of_res_Bolivia", "contry_of_res_Brazil", "contry_of_res_Burundi",
        "contry_of_res_Canada", "contry_of_res_Chile", "contry_of_res_China", "contry_of_res_Costa Rica", "contry_of_res_Cyprus",
        "contry_of_res_Czech Republic", "contry_of_res_Ecuador", "contry_of_res_Egypt", "contry_of_res_Ethiopia", "contry_of_res_Finland",
        "contry_of_res_France", "contry_of_res_Germany", "contry_of_res_Hong Kong", "contry_of_res_Iceland", "contry_of_res_India",
        "contry_of_res_Indonesia", "contry_of_res_Iran", "contry_of_res_Iraq", "contry_of_res_Ireland", "contry_of_res_Italy",
        "contry_of_res_Japan", "contry_of_res_Jordan", "contry_of_res_Kazakhstan", "contry_of_res_Lebanon", "contry_of_res_Malaysia",
        "contry_of_res_Mexico", "contry_of_res_Nepal", "contry_of_res_Netherlands", "contry_of_res_New Zealand", "contry_of_res_Nicaragua",
        "contry_of_res_Niger", "contry_of_res_Oman", "contry_of_res_Pakistan", "contry_of_res_Philippines", "contry_of_res_Portugal",
        "contry_of_res_Romania", "contry_of_res_Russia", "contry_of_res_Saudi Arabia", "contry_of_res_Serbia", "contry_of_res_Sierra Leone",
        "contry_of_res_South Africa", "contry_of_res_Spain", "contry_of_res_Sri Lanka", "contry_of_res_Sweden", "contry_of_res_Tonga",
        "contry_of_res_Turkey", "contry_of_res_Ukraine", "contry_of_res_United Arab Emirates", "contry_of_res_United Kingdom", "contry_of_res_United States",
        "contry_of_res_Uruguay", "contry_of_res_Viet Nam", "used_app_before_yes", "relation_Health care professional", "relation_Others",
        "relation_Parent", "relation_Relative", "relation_Self"
    )

    private val dataMin = floatArrayOf(
        0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 17f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f
    )

    private val dataRange = floatArrayOf(
        1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 366f, 10f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_simulation, container, false)
        initViews(view)
        setupSpinners()
        initInterpreter()
        return view
    }

    private fun initViews(view: View) {
        btnCheck = view.findViewById(R.id.btnCheck)
        inputAge = view.findViewById(R.id.input_age)
        genderSpinner = view.findViewById(R.id.spinner_gender)
        ethnicitySpinner = view.findViewById(R.id.spinner_ethnicity)
        jaundiceSpinner = view.findViewById(R.id.spinner_jaundice)
        familyAsdSpinner = view.findViewById(R.id.spinner_family_asd)
        countrySpinner = view.findViewById(R.id.spinner_country)
        usedAppSpinner = view.findViewById(R.id.spinner_used_app)
        relationSpinner = view.findViewById(R.id.spinner_relation)

        for (i in 1..10) {
            val rgId = resources.getIdentifier("rg_a$i", "id", requireContext().packageName)
            questionRadioGroups.add(view.findViewById(rgId))
        }

        btnCheck.setOnClickListener { predict() }
    }

    private fun setupSpinners() {
        genderSpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions.map { it.uppercase() }))
        ethnicitySpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ethnicityOptions))
        jaundiceSpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, yesNoOptions.map { it.replaceFirstChar { c -> c.uppercase() } }))
        familyAsdSpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, yesNoOptions.map { it.replaceFirstChar { c -> c.uppercase() } }))
        countrySpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countryOptions))
        usedAppSpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, yesNoOptions.map { it.replaceFirstChar { c -> c.uppercase() } }))
        relationSpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, relationOptions))


        genderSpinner.setText(genderOptions[0].uppercase(), false)
        ethnicitySpinner.setText(ethnicityOptions[0], false)
        jaundiceSpinner.setText(yesNoOptions[0].replaceFirstChar { it.uppercase() }, false)
        familyAsdSpinner.setText(yesNoOptions[0].replaceFirstChar { it.uppercase() }, false)
        countrySpinner.setText(countryOptions[0], false)
        usedAppSpinner.setText(yesNoOptions[0].replaceFirstChar { it.uppercase() }, false)
        relationSpinner.setText(relationOptions[0], false)
    }

    private fun initInterpreter() {
        val options = Interpreter.Options()
        options.setNumThreads(4)
        options.setUseNNAPI(true)
        interpreter = Interpreter(loadModelFile(mModelPath), options)
    }

    private fun predict() {
        if (!isAdded) return

        val inputData = gatherInput()
        val inputArray = createInputBuffer(inputData)



        val inputBuffer = Array(1) { inputArray }

        val output = Array(1) { FloatArray(1) }
        interpreter.run(inputBuffer, output)
        val probability = output[0][0]

        showResultDialog(probability)
    }

    private fun gatherInput(): Map<String, Float> {
        val data = mutableMapOf<String, Float>()
        questionRadioGroups.forEachIndexed { index, radioGroup ->
            val selectedId = radioGroup.checkedRadioButtonId
            val radioButton = radioGroup.findViewById<RadioButton>(selectedId)
            val isYes = radioButton?.text.toString().equals("Ya", ignoreCase = true)
            data["A${index + 1}_Score"] = if (isYes) 1f else 0f
        }

        data["age"] = inputAge.text.toString().toFloatOrNull() ?: 0f
        data["result"] = (1..10).sumOf { data["A${it}_Score"]?.toInt() ?: 0 }.toFloat()




        val selectedGender = genderSpinner.text.toString().lowercase()
        val sortedGender = genderOptions.sorted()
        data["gender_${sortedGender[1]}"] = if (selectedGender == sortedGender[1]) 1f else 0f


        val selectedJaundice = jaundiceSpinner.text.toString().lowercase()
        val sortedJaundice = yesNoOptions.sorted()
        data["jundice_${sortedJaundice[1]}"] = if (selectedJaundice == sortedJaundice[1]) 1f else 0f


        val selectedFamilyAsd = familyAsdSpinner.text.toString().lowercase()
        val sortedFamilyAsd = yesNoOptions.sorted()
        data["austim_${sortedFamilyAsd[1]}"] = if (selectedFamilyAsd == sortedFamilyAsd[1]) 1f else 0f


        val selectedUsedApp = usedAppSpinner.text.toString().lowercase()
        val sortedUsedApp = yesNoOptions.sorted()
        data["used_app_before_${sortedUsedApp[1]}"] = if (selectedUsedApp == sortedUsedApp[1]) 1f else 0f


        val selectedEthnicity = ethnicitySpinner.text.toString()
        val sortedEthnicity = ethnicityOptions.sorted()
        val baseEthnicity = sortedEthnicity.first()
        sortedEthnicity.forEach { ethnicity ->
            if (ethnicity != baseEthnicity) {
                data["ethnicity_$ethnicity"] = if (selectedEthnicity == ethnicity) 1f else 0f
            }
        }


        val selectedRelation = relationSpinner.text.toString()
        val sortedRelation = relationOptions.sorted()
        val baseRelation = sortedRelation.first()
        sortedRelation.forEach { relation ->
            if (relation != baseRelation) {
                data["relation_$relation"] = if (selectedRelation == relation) 1f else 0f
            }
        }


        val selectedCountry = "contry_of_res_${countrySpinner.text.toString()}"
        val sortedCountry = countryOptions.sorted()

        val baseCountry = sortedCountry.first()
        sortedCountry.forEach { country ->

            if ("contry_of_res_$country" != "contry_of_res_$baseCountry") {
                 data["contry_of_res_$country"] = if (selectedCountry == "contry_of_res_$country") 1f else 0f
            }
        }
        return data
    }

    private fun createInputBuffer(inputData: Map<String, Float>): FloatArray {
        val inputBuffer = FloatArray(expectedFeatures.size)
        for (i in expectedFeatures.indices) {
            inputBuffer[i] = inputData[expectedFeatures[i]] ?: 0f
        }

        for (i in inputBuffer.indices) {
            if (dataRange[i] != 0f) {
                inputBuffer[i] = (inputBuffer[i] - dataMin[i]) / dataRange[i]
            } else {
                inputBuffer[i] = 0f
            }
        }
        return inputBuffer
    }

    private fun showResultDialog(probability: Float) {
        if (!isAdded) return

        val title: String
        val message: String

        if (probability > 0.5) {
            title = "Hasil Prediksi: Kemungkinan ASD"
            message = "Probabilitas: ${"%.1f".format(probability * 100)}%.\n\n" +
                      "PENTING: Ini bukan diagnosis medis. Harap konsultasi dengan profesional."
        } else {
            title = "Hasil Prediksi: Kemungkinan Bukan ASD"
            message = "Probabilitas Non-ASD: ${"%.1f".format((1 - probability) * 100)}%."
        }

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = requireContext().assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
} 